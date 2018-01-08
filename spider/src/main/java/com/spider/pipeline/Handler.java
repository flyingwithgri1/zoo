package com.spider.pipeline;

import com.spider.bean.Page;

public interface Handler {
	public boolean init();
	
    public boolean shouldProcess(Page page);
    
    public void process(Page page);
}
