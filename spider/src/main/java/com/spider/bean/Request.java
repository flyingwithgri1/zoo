package com.spider.bean;

import java.util.HashMap;
import java.util.Map;

import com.spider.parser.DocTypeEnum;

public class Request {
	
	public static final String PROXY = "proxy";
	
	private int statusCode = 0;
	
	//重试次数
	private int cycleTriedTimes = 0;
	
	private DocTypeEnum docType = DocTypeEnum.UNKNOWN;
	
	private Boolean needDedup = true;
	
	private String method;
	
	private String url;
	
	private int priority;
	
	private String siteName;
	
	//队列路径：local本地队列，remote redis队列
	private String queuepath = "redis";
	
	private Map<String, Object> extras = new HashMap<String, Object>();

	public Request() {
	}
	
	public Request(String url) {
		this.url = url;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public DocTypeEnum getDocType() {
		return docType;
	}

	public void setDocType(DocTypeEnum docType) {
		this.docType = docType;
	}

	public Boolean getNeedDedup() {
		return needDedup;
	}

	public void setNeedDedup(Boolean needDedup) {
		this.needDedup = needDedup;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getPriority() {
		return priority;
	}

	public Request setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}
	
	public Object getExtra(String key) {
		if(extras == null){
			return null;
		}
		return extras.get(key);
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras.putAll(extras);
	}
	
	public Request putExtra(String key,Object value){
		this.extras.put(key, value);
		return this;
	}

	public int getCycleTriedTimes() {
		return cycleTriedTimes;
	}

	public Request setCycleTriedTimes(int cycleTriedTimes) {
		this.cycleTriedTimes = cycleTriedTimes;
		return this;
	}

	public static String getProxy() {
		return PROXY;
	}

	public String getQueuepath() {
		return queuepath;
	}

	public void setQueuepath(String queuepath) {
		this.queuepath = queuepath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cycleTriedTimes;
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result + ((extras == null) ? 0 : extras.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((needDedup == null) ? 0 : needDedup.hashCode());
		result = prime * result + priority;
		result = prime * result + ((queuepath == null) ? 0 : queuepath.hashCode());
		result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Request [statusCode=" + statusCode + ", cycleTriedTimes=" + cycleTriedTimes + ", docType=" + docType
				+ ", needDedup=" + needDedup + ", method=" + method + ", url=" + url + ", priority=" + priority
				+ ", siteName=" + siteName + ", queuepath=" + queuepath + ", extras=" + extras + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (cycleTriedTimes != other.cycleTriedTimes)
			return false;
		if (docType != other.docType)
			return false;
		if (extras == null) {
			if (other.extras != null)
				return false;
		} else if (!extras.equals(other.extras))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (needDedup == null) {
			if (other.needDedup != null)
				return false;
		} else if (!needDedup.equals(other.needDedup))
			return false;
		if (priority != other.priority)
			return false;
		if (queuepath == null) {
			if (other.queuepath != null)
				return false;
		} else if (!queuepath.equals(other.queuepath))
			return false;
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
