package com.spider.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class SiteConfig implements Cloneable{
	
	private String siteName;
	
    private String userAgent;

    private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();

    private Table<String, String, String> cookies = HashBasedTable.create();

    private String charset;
    
    private boolean seedDedup;
    
    private String queueAddress;
    
    private boolean detailDedup;
    
    private String host;
    
    /**
     * default is HTTP.
     */
    private String downloadType = "HTTP";
    
    /**
     * startUrls is the urls the crawler to start with.
     */
    private List<Request> startRequests = new ArrayList<Request>();

    private int sleepTime = 5000;

    //马上重试
    private int retryTimes = 3;

    //重试次数,返回队列
    private int cycleRetryTimes = 3;

    //超时时间
    private int timeOut = 5000;

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

    private Map<String, String> headers = new HashMap<String, String>();

    private HttpHost httpProxy;
    
    private boolean useGzip = true;
    
    private int defaultHubPriority = 1;
    
    private int defaultDetailPriority = 0;

    static {
        DEFAULT_STATUS_CODE_SET.add(HttpStatus.SC_OK);
        DEFAULT_STATUS_CODE_SET.add(HttpStatus.SC_MOVED_TEMPORARILY);
        DEFAULT_STATUS_CODE_SET.add(HttpStatus.SC_MOVED_PERMANENTLY);
    }

    /**
     * new a Site
     * 
     * @return new site
     */
    public static SiteConfig me() {
        return new SiteConfig();
    }
    
    /*@Override
    public SiteConfig clone() {
        SiteConfig clone = SiteConfig.me()
                .setAcceptStatCode(getAcceptStatCode())
                .setCharset(getCharset())
                .setDomain(getDomain())
                .setCycleRetryTimes(getCycleRetryTimes())
                .setDefaultDetailPriority(getDefaultDetailPriority())
                .setDefaultHubPriority(getDefaultHubPriority())
                .setRetryTimes(getRetryTimes())
                .setSleepTime(getSleepTime())
                .setTimeOut(getTimeOut())
                .setUseGzip(isUseGzip())
                .setUserAgent(getUserAgent());
        return clone;
    }*/

    
    
    public boolean getSeedDedup() {
		return seedDedup;
	}

	public SiteConfig setSeedDedup(boolean seedDedup) {
		this.seedDedup = seedDedup;
		return this;
	}

	public String getQueueAddress() {
		return queueAddress;
	}

	public SiteConfig setQueueAddress(String queueAddress) {
		this.queueAddress = queueAddress;
		return this;
	}

	public boolean getDetailDedup() {
		return detailDedup;
	}

	public SiteConfig setDetailDedup(boolean detailDedup) {
		this.detailDedup = detailDedup;
		return this;
	}

	public String getHost() {
		return host;
	}

	public SiteConfig setHost(String host) {
		this.host = host;
		return this;
	}

	/**
     * Add a cookie with domain {@link #getDomain()}
     * 
     * @param name
     * @param value
     * @return this
     */
    public SiteConfig addCookie(String name, String value) {
        defaultCookies.put(name, value);
        return this;
    }


    /**
     * get cookies
     * 
     * @return get cookies
     */
    public Map<String, String> getCookies() {
        return defaultCookies;
    }

    public Map<String,Map<String, String>> getAllCookies(){
    	return cookies.rowMap();
    }

    /**
     * get user agent
     * 
     * @return user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * set user agent
     * 
     * @param userAgent
     *            userAgent
     * @return this
     */
    public SiteConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * get charset set manually
     * 
     * @return charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Set charset of page manually.<br>
     * When charset is not set or set to null, it can be auto detected by Http
     * header.
     * 
     * @param charset
     * @return this
     */
    public SiteConfig setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    /**
     * set timeout for downloader in ms
     * 
     * @param timeOut
     */
    public SiteConfig setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    /**
     * get acceptStatCode
     * 
     * @return acceptStatCode
     */
    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }

    public String getSiteName() {
		return siteName;
	}

	public SiteConfig setSiteName(String siteName) {
		this.siteName = siteName;
		return this;
	}

	/**
     * Set acceptStatCode.<br>
     * When status code of http response is in acceptStatCodes, it will be
     * processed.<br>
     * {200} by default.<br>
     * It is not necessarily to be set.<br>
     * 
     * @param acceptStatCode
     * @return this
     */
    public SiteConfig setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
        return this;
    }

    public List<Request> getStartRequests() {
        return startRequests;
    }

    public int getDefaultHubPriority() {
        return defaultHubPriority;
    }
    
    public String getDownloadType() {
		return downloadType;
	}

	public SiteConfig setDownloadType(String downloadType) {
		this.downloadType = downloadType;
		return this;
	}

	public SiteConfig setDefaultHubPriority(int defaultHubPriority) {
        this.defaultHubPriority = defaultHubPriority;
        return this;
    }

    public int getDefaultDetailPriority() {
        return defaultDetailPriority;
    }

    public SiteConfig setDefaultDetailPriority(int defaultDetailPriority) {
        this.defaultDetailPriority = defaultDetailPriority;
        return this;
    }

    /**
     * Get the interval between the processing of two pages.<br>
     * Time unit is mills second.<br>
     * 
     * @return the interval between the processing of two pages,
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * Set the interval between the processing of two pages.<br>
     * Time unit is micro seconds.<br>
     * 
     * @param sleepTime
     * @return this
     */
    public SiteConfig setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    /**
     * Get retry times immediately when download fail, 0 by default.<br>
     * 
     * @return retry times when download fail
     */
    public int getRetryTimes() {
        return retryTimes;
    }

    /**
     * Set retry times when download fail, 0 by default.<br>
     * 
     * @return this
     */
    public SiteConfig setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Put an Http header for downloader. <br/>
     * Use {@link #addCookie(String, String)} for cookie and
     * {@link #setUserAgent(String)} for user-agent. <br/>
     * 
     * @param key
     *            key of http header, there are some keys constant in
     *            {@link HeaderConst}
     * @param value
     *            value of header
     * @return
     */
    public SiteConfig addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * When cycleRetryTimes is more than 0, it will add back to scheduler and
     * try download again. <br>
     * 
     * @return retry times when download fail
     */
    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    /**
     * Set cycleRetryTimes times when download fail, 0 by default. Only work in
     * RedisScheduler. <br>
     * 
     * @return this
     */
    public SiteConfig setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
        return this;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    /**
     * Whether use gzip. <br>
     * Default is true, you can set it to false to disable gzip.
     * 
     * @param useGzip
     * @return
     */
    public SiteConfig setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }

	@Override
	public String toString() {
		return "SiteConfig [siteName=" + siteName + ", userAgent=" + userAgent + ", defaultCookies=" + defaultCookies
				+ ", cookies=" + cookies + ", charset=" + charset + ", seedDedup=" + seedDedup + ", queueAddress="
				+ queueAddress + ", detailDedup=" + detailDedup + ", host=" + host + ", downloadType=" + downloadType
				+ ", startRequests=" + startRequests + ", sleepTime=" + sleepTime + ", retryTimes=" + retryTimes
				+ ", cycleRetryTimes=" + cycleRetryTimes + ", timeOut=" + timeOut + ", acceptStatCode=" + acceptStatCode
				+ ", headers=" + headers + ", httpProxy=" + httpProxy + ", useGzip=" + useGzip + ", defaultHubPriority="
				+ defaultHubPriority + ", defaultDetailPriority=" + defaultDetailPriority + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acceptStatCode == null) ? 0 : acceptStatCode.hashCode());
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + ((cookies == null) ? 0 : cookies.hashCode());
		result = prime * result + cycleRetryTimes;
		result = prime * result + ((defaultCookies == null) ? 0 : defaultCookies.hashCode());
		result = prime * result + defaultDetailPriority;
		result = prime * result + defaultHubPriority;
		result = prime * result + (detailDedup ? 1231 : 1237);
		result = prime * result + ((downloadType == null) ? 0 : downloadType.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((httpProxy == null) ? 0 : httpProxy.hashCode());
		result = prime * result + ((queueAddress == null) ? 0 : queueAddress.hashCode());
		result = prime * result + retryTimes;
		result = prime * result + (seedDedup ? 1231 : 1237);
		result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
		result = prime * result + sleepTime;
		result = prime * result + ((startRequests == null) ? 0 : startRequests.hashCode());
		result = prime * result + timeOut;
		result = prime * result + (useGzip ? 1231 : 1237);
		result = prime * result + ((userAgent == null) ? 0 : userAgent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SiteConfig other = (SiteConfig) obj;
		if (acceptStatCode == null) {
			if (other.acceptStatCode != null)
				return false;
		} else if (!acceptStatCode.equals(other.acceptStatCode))
			return false;
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (cookies == null) {
			if (other.cookies != null)
				return false;
		} else if (!cookies.equals(other.cookies))
			return false;
		if (cycleRetryTimes != other.cycleRetryTimes)
			return false;
		if (defaultCookies == null) {
			if (other.defaultCookies != null)
				return false;
		} else if (!defaultCookies.equals(other.defaultCookies))
			return false;
		if (defaultDetailPriority != other.defaultDetailPriority)
			return false;
		if (defaultHubPriority != other.defaultHubPriority)
			return false;
		if (detailDedup != other.detailDedup)
			return false;
		if (downloadType == null) {
			if (other.downloadType != null)
				return false;
		} else if (!downloadType.equals(other.downloadType))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (httpProxy == null) {
			if (other.httpProxy != null)
				return false;
		} else if (!httpProxy.equals(other.httpProxy))
			return false;
		if (queueAddress == null) {
			if (other.queueAddress != null)
				return false;
		} else if (!queueAddress.equals(other.queueAddress))
			return false;
		if (retryTimes != other.retryTimes)
			return false;
		if (seedDedup != other.seedDedup)
			return false;
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		if (sleepTime != other.sleepTime)
			return false;
		if (startRequests == null) {
			if (other.startRequests != null)
				return false;
		} else if (!startRequests.equals(other.startRequests))
			return false;
		if (timeOut != other.timeOut)
			return false;
		if (useGzip != other.useGzip)
			return false;
		if (userAgent == null) {
			if (other.userAgent != null)
				return false;
		} else if (!userAgent.equals(other.userAgent))
			return false;
		return true;
	}
}
