package com.spider.scheduler;

import com.spider.bean.Request;

public interface Scheduler {

	public void push(Request request);
	
    public Request poll();
    
}
