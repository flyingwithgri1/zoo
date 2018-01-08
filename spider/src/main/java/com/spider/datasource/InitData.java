package com.spider.datasource;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.spider.bean.Request;
import com.spider.bean.SiteConfigManager;
import com.spider.parser.DocTypeEnum;
import com.common.util.JedisUtil;
import  com.common.util.MongoDBUtil;
import com.spider.utils.SpiderConfig;

public class InitData {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	public void initData(){
		
		/*Request request = new Request();
//		request.setUrl("http://news.sohu.com/guoneixinwen.shtml");//搜狐新闻国内 已解决
//		request.setUrl("http://news.sohu.com/shehuixinwen.shtml");//搜狐新闻社会 已解决
//		request.setUrl("http://mil.sohu.com/");//军事
//		request.setUrl("http://news.sohu.com/scroll/");//滚动新闻
		request.setUrl("http://www.ccstock.cn/");//中国军网 高层动态
		request.setSiteName("中国资本证券网");
		request.setNeedDedup(false);
		request.setDocType(DocTypeEnum.HUB_PAGE);
		JedisUtil.lpush(SpiderConfig.redispriorityQueuePlus, JSONObject.toJSONString(request), SpiderConfig.redisSelect);*/
	
		initSeed();

	}

	private void initSeed(){
		Document param = new Document();
		Document p2 = new Document();
		p2.put("$in", Arrays.asList(SpiderConfig.seeds.split(",")));
		param.put("siteId", p2);
		int i= 0;
		MongoCursor<Document> iter = MongoDBUtil.getCollection(SpiderConfig.mongoDBName, "category_dic_collect").find(param).iterator();
		while(iter.hasNext()){
			Document document = iter.next();
			Request request = new Request();
			request.setUrl(document.getString("url"));
			request.setSiteName(document.getString("siteName"));
			request.setNeedDedup(false);
			request.setDocType(DocTypeEnum.HUB_PAGE);
			request.putExtra("siteId", document.getString("siteId"));
			if(!SiteConfigManager.INSTANCE.siteConfigMap.containsKey(document.getString("siteName"))){
				continue;
			}
			request.putExtra("siteName", document.getString("siteName"));
			request.putExtra("siteTypeId", document.getString("siteTypeId"));
			request.putExtra("siteTypeName", document.getString("siteTypeName"));
			request.putExtra("typeId", document.getString("typeId"));
			request.putExtra("typeName", document.getString("typeName"));
			request.putExtra("categoryName", document.getString("categoryName"));
			request.putExtra("categoryId", document.getString("categoryId"));
			request.putExtra("isNew", "1");
			request.putExtra("crawlrepeat", "0");
			
			JedisUtil.lpush(SpiderConfig.redispriorityQueuePlus, JSONObject.toJSONString(request), SpiderConfig.redisSelect);
			logger.info(JSONObject.toJSONString(request));
			i++;
		}
		logger.info("共加载"+i+"个种子页");
	}
	
	
	public static void main(String[] args) {
		Document param = new Document();
		Document p2 = new Document();
		p2.put("$in","50".split(","));
		param.put("siteId", p2);
		MongoClient mongoClient = new MongoClient("10.9.46.190", SpiderConfig.mongoPort);
		MongoCursor<Document> iter = MongoDBUtil.getCollection(SpiderConfig.mongoDBName, "category_dic_collect").find(param).iterator();
		while(iter.hasNext()){
			Document document = iter.next();
			Request request = new Request();
			request.setUrl("http://news.baidu.com/");
			request.setSiteName(document.getString("siteName"));
			request.setNeedDedup(false);
			request.setDocType(DocTypeEnum.HUB_PAGE);
			request.putExtra("siteId", document.getString("siteId"));
			request.putExtra("siteName", document.getString("siteName"));
			request.putExtra("siteTypeId", document.getString("siteTypeId"));
			request.putExtra("siteTypeName", document.getString("siteTypeName"));
			request.putExtra("typeId", document.getString("typeId"));
			request.putExtra("typeName", document.getString("typeName"));
			request.putExtra("categoryName", document.getString("categoryName"));
			request.putExtra("categoryId", document.getString("categoryId"));
			request.putExtra("isNew", "1");
			request.putExtra("crawlrepeat", "0");
			JedisUtil.lpush(SpiderConfig.redispriorityQueuePlus, JSONObject.toJSONString(request), SpiderConfig.redisSelect);
		}
	}
}
