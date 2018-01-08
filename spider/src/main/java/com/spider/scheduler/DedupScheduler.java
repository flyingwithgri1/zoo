package com.spider.scheduler;

import org.apache.log4j.Logger;

import com.spider.bean.Request;

public abstract class DedupScheduler implements Scheduler{
	protected Logger logger = Logger.getLogger(getClass());
	
	//布隆过滤器判重
	private DuplicateChecker duplicateChecker = new BloomFilterDuplicateChecker(10000000);
	
	//redis判重
	private JedisDedupChecker JedisDedupChecker = new JedisDedupChecker();
	
	@Override
	public void push(Request request) {
		//判重
		if(shouldReserved(request)){ 
			 if(!duplicateChecker.isDuplicate(request) && !JedisDedupChecker.isDuplicate(request)){
				 pushWhenNoDuplicate(request);
			 }else{
				 logger.info("url is duplicate " + request.getUrl());
			 }
		}else{
			pushWhenNoDuplicate(request);
		}
	}

	@Override
	public abstract Request poll();
	
	public abstract void pushWhenNoDuplicate(Request request);
	
	protected boolean shouldReserved(Request request) {
		return request.getCycleTriedTimes() != 0 ? false : request.getNeedDedup();
	}
}
