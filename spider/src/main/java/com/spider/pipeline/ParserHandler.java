package com.spider.pipeline;

import com.spider.bean.Page;
import com.spider.parser.ParserManager;

public class ParserHandler implements Handler {
    private ParserManager parserManager;
    
    public ParserHandler() {
        init();
    }

	public boolean init() {
		parserManager = new ParserManager();
		parserManager.init();
		return false;
	}
	
	public boolean shouldProcess(Page page) {
		//do something
		return true;
	}

    public void process(Page page) {
        parserManager.parse(page);
    }
}
