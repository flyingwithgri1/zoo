package com.spider.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.selector.Html;
import com.spider.selector.Selectable;
import com.common.util.CommonUtil;
import com.common.util.HTMLEscapeUtil;
import com.common.util.MongoDBUtil;
import com.spider.utils.SpiderConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BaiduNewsParser extends PropertyParser{

	@Autowired
	private MongoDBUtil mongoDBUtil;

	@Override
	protected void parseHubPage(Page page) {
		List<Request> links = new ArrayList<Request>();
		Html html = page.getHtml();
		List<Selectable> nodes = html.xpath("//div[@class='index-list-item-container']/div/@id").nodes();
		
		for(Selectable item : nodes){
			Request request = new Request();
			String urlId = item.get();
			if(StringUtils.isBlank(urlId)){
				continue;
			}
			request.setUrl("https://news.baidu.com/news#/detail/" + urlId);
			request.setDocType(DocTypeEnum.DETAIL_PAGE);
			request.setSiteName(page.getRequest().getSiteName());
			request.setExtras(page.getRequest().getExtras());
			request.setNeedDedup(true);
			links.add(request);
			logger.info(request.getUrl());
		}
		composeTargetDetailRequests(links, page);
	}

	@Override
	protected void parseDetailPage(Page page) {
		Map<String, Object> extra = page.getRequest().getExtras();
		String imgRegex = "<img[^<>]*?src=['\"]([^\"'<>\\s]+)['\"].*>";
		Document document = new Document();
		NotNullData.setNotNullData(document, extra);
		Pattern imgPattern = Pattern.compile(imgRegex);
		Html html= page.getHtml();
		String title = html.xpath("//div[@class='detail-content-header']/h2/text()").get();
		document.put("title", title);
		String sourceSiteName = html.xpath("//div[@class='header-info']/span[1]/text()").get();
		if(!StringUtils.isBlank(sourceSiteName)){
			document.put("sourceSiteName", sourceSiteName);
		}
		String publishDateTime = html.xpath("//div[@class='header-info']/span[@style]/text()").get();
		String content = html.xpath("//div[@id='newsDetailContent']").get();
		if(!StringUtils.isBlank(publishDateTime)){
			publishDateTime = CommonUtil.convertDate(publishDateTime);
			document.put("publishDateTime", publishDateTime);
		}
		if(StringUtils.isBlank(content)) {
			return;
		}
		
		content = content.replaceAll("<script.*>[\\w\\W]*</script>","").replaceAll("<style.*>[\\w\\W]*</style>", "").replace("您的浏览器暂时无法播放此视频.", "");
		content = HTMLEscapeUtil.htmlEscape2Str(content);
		Matcher imgMatcher = imgPattern.matcher(content);
		JSONArray jImage = new JSONArray();
		while(imgMatcher.find()){
			String imgLable = imgMatcher.group();
			String imgUrl = imgMatcher.group(1);
			JSONObject img = new JSONObject();
			img.put("url", imgUrl);
			img.put("title","");
			jImage.add(img);
			content = content.replace(imgLable, "###"+imgUrl+"###");
		}
		content = content.replaceAll("</?[^>]+>", "");
		document.put("content", content);
		String joinCount = html.xpath("//span[@class='up-container']/text()").get();
		if(StringUtils.isBlank(joinCount)){
			joinCount = "0";
		}
		document.put("joinCount", joinCount);
		document.put("commentCount", "0");
		document.put("entityUrl", page.getRequest().getUrl());
		document.put("picAndName",jImage);
		mongoDBUtil.getDBCollection("", "c_gd_news_basicinfo").insertOne(document);
		mongoDBUtil.getDBCollection(SpiderConfig.mongoDBName, "c_gd_news_basicinfo_add").insertOne(document);
		logger.info(document);
	}

	
}
