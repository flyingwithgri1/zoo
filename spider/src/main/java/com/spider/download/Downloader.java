package com.spider.download;

import com.spider.bean.Page;
import com.spider.bean.Request;

public interface Downloader {
	
	public Page download(Request request);
}
