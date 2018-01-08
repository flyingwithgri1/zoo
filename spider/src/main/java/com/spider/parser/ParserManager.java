package com.spider.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spider.bean.Page;
import com.spider.bean.SiteConfigManager;

public class ParserManager {

	protected Logger logger = Logger.getLogger(getClass());

	private Map<String,  Parser> parserMap = new LinkedHashMap<String, Parser>();

	public void init() {
		registerParser("百度新闻",BaiduNewsParser.class.newInstance());
	}

	public void parse(Page page) {
		Parser parser = getParser(page);
		parser.parse(page);
	}

	protected Parser getParser(Page page) {
		String siteName = SiteConfigManager.getSiteConfig(page.getRequest().getSiteName()).getSiteName();
		if (parserMap.containsKey(siteName)) {
			return parserMap.get(siteName);
		} else {
			return null;
		}
	}

	protected void registerParser(String parserTarget, Parser parser) {
		parserMap.put(parserTarget, parser);
	}
}
