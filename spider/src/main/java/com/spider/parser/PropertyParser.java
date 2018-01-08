package com.spider.parser;

import java.util.List;

import org.apache.log4j.Logger;

import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.utils.UrlUtils;

public abstract class PropertyParser implements Parser {
	protected Logger logger = Logger.getLogger(getClass());
	
	@Override
    public void parse(Page page) {
        if (shouldParse(page)) {
            if (page.getDocType() == DocTypeEnum.HUB_PAGE) {
               parseHubPage(page);
            } else if (page.getDocType() == DocTypeEnum.DETAIL_PAGE) {
                parseDetailPage(page);
            }
        }
    }

    protected abstract void parseHubPage(Page page);

    protected abstract void parseDetailPage(Page page);

    protected  void composeTargetDetailRequests(List<Request> urls, Page page) {
        for (Request item : urls) {
        	try{
        		Request request = new Request(UrlUtils.canonicalizeUrl(item.getUrl(), page.getRequest().getUrl()));
                request.setDocType(item.getDocType());
                request.setSiteName(item.getSiteName());
                request.setPriority(item.getPriority());
                request.setExtras(item.getExtras());
                request.setNeedDedup(item.getNeedDedup());
                request.setUrl(item.getUrl());
                request.setQueuepath(item.getQueuepath());
                request.setSiteName(page.getRequest().getSiteName());
                page.addTargetRequest(request);
        	}catch (Exception e) {
        		logger.error("add request error:" + item,e);
			}
        }
    }
    
    
    public boolean shouldParse(Page page) {
        return true;
    }
    
    
    
}