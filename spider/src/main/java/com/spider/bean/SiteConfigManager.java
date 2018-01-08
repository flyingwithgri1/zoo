package com.spider.bean;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spider.utils.XMLParser;

public class SiteConfigManager {
//	public static String pathProperty = System.getProperty("user.dir") + File.separator + "siteConfig.xml";
	
	public static String pathProperty = System.getProperty("user.dir") + File.separator +"src" + File.separator + "siteConfig.xml";
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static Object lock = new Object();
	public static SiteConfigManager INSTANCE;
	
	public Map<String, SiteConfig> siteConfigMap = new LinkedHashMap<String, SiteConfig>();
	
	private SiteConfig defaultSiteConfig = new SiteConfig();

	static{
		synchronized (lock) {
			if (INSTANCE == null) {
				INSTANCE = new SiteConfigManager(pathProperty);
			}
		}
	}
	
	private SiteConfigManager(String siteXmlPath) {
		init(siteXmlPath);
	}

	private boolean init(String siteXmlPath) {
		List<HashMap<String, String>> maplist = XMLParser.getXMLParser().readStringXmlOut(siteXmlPath);
		for (HashMap<String, String> map : maplist) {
			SiteConfig config = SiteConfig.me()
					.setSiteName(map.get("siteName"))
					.setSleepTime(Integer.parseInt(map.get("sleep")))
					.setSeedDedup(Boolean.parseBoolean(map.get("seedDedup")))
					.setCharset(map.get("charset"))
					.setQueueAddress(map.get("queueAddress"))
					.setCycleRetryTimes(Integer.parseInt(map.get("cycleRetryTimes")))
					.setDetailDedup(Boolean.parseBoolean(map.get("detailDedup")))
					.setHost(map.get("host"))
					.setDownloadType(map.get("downloadType"))
					.setUserAgent(map.get("userAgent"))
					.setTimeOut(Integer.parseInt(map.get("timeOut")))
					.setRetryTimes(Integer.parseInt(map.get("retryTimes")));
			if(!StringUtils.isBlank(map.get("cookie"))){
				config.addCookie("Cookie", map.get("cookie"));
			}
			registerSiteConfig(config);
		}
		return true;
	}
		
	private void registerSiteConfig(SiteConfig siteConfig) {
		if (siteConfigMap.containsKey(siteConfig.getSiteName())) {
			logger.error("Site has been registered:\t" + siteConfig.getSiteName());
		}
		siteConfigMap.put(siteConfig.getSiteName(), siteConfig);
	}
	
	public static SiteConfig getSiteConfig(String siteName) {
		if (INSTANCE.siteConfigMap.containsKey(siteName)) {
			return INSTANCE.siteConfigMap.get(siteName);
		} else {
			return INSTANCE.defaultSiteConfig;
		}
	}

}
