package com.spider.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLParser {
//	public static String pathProperty = System.getProperty("user.dir") + File.separator + "siteConfig.xml";
	
//	public static String pathProperty = System.getProperty("user.dir") + File.separator +"src" + File.separator + "siteConfig.xml";
	
	protected Logger logger = Logger.getLogger(getClass());

	private XMLParser() {
	}
	
	static class Dom4jParser {
		private static XMLParser instance = new XMLParser();
	}

	public static XMLParser getXMLParser() {
		return Dom4jParser.instance;
	}

	public List<HashMap<String, String>> readStringXmlOut(String path) {
		List<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(readFile(path));
			Element rootElt = doc.getRootElement();

			Iterator<?> iter = rootElt.elementIterator("site");
			while (iter.hasNext()) {
				HashMap<String, String> map = new HashMap<String, String>();
				Element recordEle = (Element) iter.next();
				
				String siteName = recordEle.elementTextTrim("siteName");
				map.put("siteName", siteName);
				String host = recordEle.elementTextTrim("host");
				map.put("host", host);

				String sleep = recordEle.elementTextTrim("sleep");
				map.put("sleep", sleep);

				String timeOut = recordEle.elementTextTrim("timeOut");
				map.put("timeOut", timeOut);

				String userAgent = recordEle.elementTextTrim("userAgent");
				map.put("userAgent", userAgent);

				String queueAddress = recordEle.elementTextTrim("queueAddress");
				map.put("queueAddress", queueAddress);

				String seedDedup = recordEle.elementTextTrim("seedDedup");
				map.put("seedDedup", seedDedup);
				
				String detailDedup = recordEle.elementTextTrim("detailDedup");
				map.put("detailDedup", detailDedup);
				
				String cycleRetryTimes = recordEle.elementTextTrim("cycleRetryTimes");
				map.put("cycleRetryTimes", cycleRetryTimes);
				
				String downloadType = recordEle.elementTextTrim("downloadType");
				map.put("downloadType", downloadType);
				
				String header = recordEle.elementTextTrim("header");
				map.put("header", header);
				
				String charset = recordEle.elementTextTrim("charset");
				map.put("charset", charset);
				
				String cookie = recordEle.elementTextTrim("cookie");
				if(!StringUtils.isBlank(cookie)){
					map.put("cookie", cookie);
				}
				
				String retryTimes = recordEle.elementTextTrim("retryTimes");
				map.put("retryTimes", retryTimes);
				maplist.add(map);
			}

		} catch (DocumentException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		}
		return maplist;
	}

	public String readFile(String path) {
		String lines = "";
		try {
			lines = FileUtils.readFileToString(new File(path), "UTF-8");
		} catch (IOException e) {
			logger.error("", e);
		} 
		return lines;
	}
}
