package com.spider.bean;

import java.util.ArrayList;
import java.util.List;

import com.spider.parser.DocTypeEnum;
import com.spider.selector.Html;
import com.spider.utils.HTMLEscapeUtil;
import com.spider.utils.UrlUtils;

public class Page {

	//http状态码
	private int statusCode;

	//是否需要重试
    private boolean needCycleRetry = false;
    
    private Request request;

    //当前页面解析出的url
    private List<Request> targetRequests = new ArrayList<Request>();
    
    //html xpath解析
    private Html html;
    
    //html源码
    private String content;
    
    private boolean needStore = true;
    
	public boolean getNeedStore() {
		return needStore;
	}

	public void setNeedStore(boolean needStore) {
		this.needStore = needStore;
	}

	public String getContent() {
		return content;
	}

	/*public void setContent(String content) {
		this.content = UrlUtils.fixAllRelativeHrefs(content, request.getUrl());
	}*/
	public void setContent(String content) {
		this.content = content;
	}

	public Html getHtml() {
		if (this.html == null) {
			this.html = new Html(this.content);
        }
		return html;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isNeedCycleRetry() {
		return needCycleRetry;
	}

	public void setNeedCycleRetry(boolean needCycleRetry) {
		this.needCycleRetry = needCycleRetry;
	}

	public List<Request> getTargetRequests() {
		return targetRequests;
	}

	public void setTargetRequest(List<Request> targetRequests) {
		this.targetRequests = targetRequests;
	}

    public void addTargetRequest(Request request){
    	this.targetRequests.add(request);
    }

	public DocTypeEnum getDocType() {
		return request.getDocType();
	}
}
