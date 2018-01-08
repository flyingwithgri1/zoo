package com.spider.main;

import java.util.ArrayList;
import java.util.List;

import com.common.util.CountableThreadPool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.bean.SiteConfigManager;
import com.spider.datasource.InitData;
import com.spider.download.DownloadManager;
import com.spider.download.Downloader;
import com.spider.pipeline.Handler;
import com.spider.pipeline.ParserHandler;
import com.spider.pipeline.StorageHandler;
import com.spider.scheduler.Scheduler;
import com.spider.scheduler.SchedulerManager;
import com.spider.utils.SpiderConfig;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class); 
	private static Downloader downloader = null;
	private List<Handler> pipeline = new ArrayList<Handler>();
	private CountableThreadPool service = null;
	private Scheduler scheduler = null;
	private InitData initData = null;
	
	public static void main(String[] args) {
//		logger.info(args[0]);
		Main main = new Main();
		SpiderConfig.init();
//		SpiderConfig.seeds = args[0];
		SpiderConfig.seeds = "46";
//		SpiderConfig.seeds = "66,67,68";
		logger.info(SpiderConfig.seeds);
		main.init();
		main.go();
	}
	
	private void init(){
		logger.info("初始化相关组件");
		//配置
		//管道（包含解析和存储）
		pipeline.add(new ParserHandler());
		pipeline.add(new StorageHandler());
		//下载器
		downloader = new DownloadManager();
		//队列调度管理器 
		scheduler = new SchedulerManager();
		//线程池
		service = new CountableThreadPool(SpiderConfig.threadNum);
		//初始化种子队列
		initData = new InitData();
		initData.initData();
	}
	
	private void go(){
		while(!Thread.currentThread().isInterrupted()){
			final Request request = scheduler.poll();
			if(request == null){
				sleep(2000);
				continue;
			}
			//放入正在爬取的队列中
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
                        processRequest(request);
                    } catch (Exception e) {
                        logger.error("process request " + request + " error", e);
                    } finally {
                    	
                    }
				}
			});
		}
	}
	
	protected void processRequest(Request request) {
		Page page = downloader.download(request);
		if (page == null) {
	        return;
        } else if (page.isNeedCycleRetry()) {//下载出错重试
            extractAndAddRequests(page);
        } else {
            for (Handler handler : pipeline) {
                if (handler.shouldProcess(page)) {
                    handler.process(page);
                }
            }
            //解析完成后，新解析出来的url压入队列
            extractAndAddRequests(page);
        }
		sleep(SiteConfigManager.getSiteConfig(request.getSiteName()).getSleepTime());
	}

	protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	protected void extractAndAddRequests(Page page) {
        if (CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                addRequest(request);
            }
        }
    }
	
	private void addRequest(Request request) {
        scheduler.push(request);
    }
}
