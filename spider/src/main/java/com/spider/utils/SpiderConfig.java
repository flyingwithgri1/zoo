package com.spider.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SpiderConfig {

	private static Logger logger = Logger.getLogger(SpiderConfig.class);
	public static String spiderName = "spider";
	public static String hostIp = "";
	public static String hostName = "";
	
	public static String redisHost = "";
	public static int redisPort = 6379;
	public static String redisPassword = "";
	public static int redisSelect = 0;
	public static String redisDedup = "DEDUP";
	public static String redisNoPriorityQueue = "NOPRIORITYQUEUE";
	public static String redispriorityQueuePlus = "PRIORITYQUEUEPLUS";
	public static String redispriorityQueueMinus = "PRIORITYQUEUEMINUS";
	
	public static String mongoHost = "";
	public static int mongoPort = 27017;
	public static String mongoDBName = "";
	public static String mongoUser = "";
	public static String mongoPassword = "";
	
	public static boolean isInit = false;
	public static String phantomjsPath = "";
	
	public static String timing ;
	public static String seeds = "";
	public static int threadNum = 1;
	
	public static String emailAccount = "";
	public static String emailPasswd = "";
	public static String receiveMailAccount = "";
	public static String[] receiveMailAccounts = null; 
	public static String emailFromUserName = "";
	public static String emailSubject = "";
	public static String emailContent = "";
	public static void init(){
		InputStream is = null;
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
			String pathProperty = System.getProperty("user.dir") + File.separator +"src" + File.separator + "config.ini";
//			String pathProperty = System.getProperty("user.dir") + File.separator + "config.ini";
			is = new FileInputStream(new File(pathProperty)); 
			Properties prop = new Properties();
			prop.load(is);
			
			spiderName = prop.getProperty("spider_name");
			phantomjsPath = prop.getProperty("phantomjs_path");
			redisDedup = spiderName+"_"+prop.getProperty("redis_dedup");
			redisNoPriorityQueue = spiderName+"_"+prop.getProperty("redis_no_priority_queue","NOPRIORITYQUEUE");
			redispriorityQueuePlus = spiderName+"_"+prop.getProperty("redis_priority_queue_plus","PRIORITYQUEUEPLUS");
			redispriorityQueueMinus = spiderName+"_"+prop.getProperty("redis_priority_queue_minus","PRIORITYQUEUEMINUS");
			redisSelect = Integer.parseInt(prop.getProperty("redis_select").toString());
			
			redisHost = prop.getProperty("redis_host", "127.0.0.1").toString();
			redisPort = Integer.parseInt(prop.getProperty("redis_port", "6379").toString());
			redisPassword = prop.getProperty("redis_password");
			mongoHost = prop.getProperty("mongo_host", "127.0.0.1").toString();
			mongoPort = Integer.parseInt(prop.getProperty("mongo_port", "27017").toString());
			mongoDBName = prop.getProperty("mongo_db_name").toString();
			mongoUser = prop.getProperty("mongo_user");
			mongoPassword = prop.getProperty("mongo_password");
			
			isInit = Boolean.parseBoolean(prop.getProperty("is_init","false"));
			threadNum = Integer.parseInt(prop.getProperty("thread_num", "1").toString());
			timing = prop.getProperty("timing","0 0/20 * * * ? *");
			/*emailAccount = prop.getProperty("email_account");
			emailPasswd = prop.getProperty("email_passwd");
			receiveMailAccount = prop.getProperty("receive_mail_account");
			receiveMailAccounts = prop.getProperty("receive_mail_accounts").split(",");
			emailFromUserName = prop.getProperty("email_from_username");
			emailSubject = prop.getProperty("email_subject");
			emailContent = prop.getProperty("emailContent");*/
			logger.info("[spiderName:"+spiderName+"; timing:"+timing+"; redisHost:"+redisHost+"; redisPort:"+redisPort+"; redisPassword:"+redisPassword+
					"; redisSelect:"+redisSelect+"; mongoHost:"+mongoHost+"; mongoPort:"+mongoPort+";mongoDBName:"+ mongoDBName +"; mongoUser:"+mongoUser+"; mongoPassword:"+mongoPassword+ "; isInit:" + isInit +
					"; redisDedup:"+redisDedup+"; redisNoPriorityQueue:"+redisNoPriorityQueue+"; redispriorityQueuePlus:"+redispriorityQueuePlus+"; redispriorityQueueMinus:"+redispriorityQueueMinus+";]");
		} catch (IOException e) {
			logger.info("加载配置文件出错",e);
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void main(String[] args) {
		SpiderConfig.init();
	}

}
