package com.spider.download;

import java.util.LinkedHashMap;
import java.util.Map;

import com.common.util.JedisUtil;
import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.bean.SiteConfig;
import com.spider.bean.SiteConfigManager;

public class DownloadManager implements Downloader{

	private Map<String, Downloader> download = new LinkedHashMap<String, Downloader>();
	
	public DownloadManager() {
		try {
			registerDownload("HTTP", HttpClientDownloader.class.newInstance());
			registerDownload("PLUGINUNIT", PhantomJSDownloader.class.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
	
	private void registerDownload(String key,Downloader downloader){
		download.put(key, downloader);
	}
	
	public Downloader getDownloader(Request request){
		return download.get(SiteConfigManager.getSiteConfig(request.getSiteName()).getDownloadType());
	}
	
	@Override
	public Page download(Request request) {
		Downloader downloader = getDownloader(request);
		return downloader.download(request);
	}

}
