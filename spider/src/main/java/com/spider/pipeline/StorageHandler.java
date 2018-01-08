package com.spider.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.spider.bean.Page;
import com.spider.parser.DocTypeEnum;
import com.spider.utils.MongoDBUtil;

public class StorageHandler implements Handler {
	protected Logger logger = Logger.getLogger(getClass());
	
	public boolean shouldProcess(Page page) {
		return page.getDocType() == DocTypeEnum.DETAIL_PAGE && page.getNeedStore();
	}
	
	/* 
	 * 根据数据类型，进行不同的存储
	 *  */
	public void process(Page page) {
		try{
			logger.info("store success");
			//1从正在爬取中的队列删除
			//2放入判重队列
		}catch (Exception e) {
			logger.error("存储出错" + page.getRequest().getUrl() ,e);
		}
	}
	
	public boolean init() {
		return true;
	}
	
}
