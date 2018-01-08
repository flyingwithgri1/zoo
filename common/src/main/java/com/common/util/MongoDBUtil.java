package com.common.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
	private static MongoClientOptions options;
	private static MongoClient mongoClient = null;
	private MongoDBUtil() {}

	static {
		synchronized (MongoDBUtil.class) {
			initDBPrompties();
		}
	}

	public static MongoCollection<Document> getCollection(String dbName,String collName) {
		MongoDatabase db = mongoClient.getDatabase(dbName);
		return db.getCollection(collName);
	}
	
	public static MongoCollection<Document> getDBCollection(String dbName,String collName) {
		return mongoClient.getDatabase(dbName).getCollection(collName);
	}

	 /** 
	 * 初始化连接池 
	 */  
	private static void initDBPrompties() {  
		/*try {
			if(StringUtils.isBlank(SpiderConfig.mongoUser) || StringUtils.isBlank(SpiderConfig.mongoPassword)){
				mongoClient = new MongoClient(SpiderConfig.mongoHost, SpiderConfig.mongoPort);
			}else{
				ServerAddress serverAddress = new ServerAddress(SpiderConfig.mongoHost, SpiderConfig.mongoPort);
				List<ServerAddress> addrs = new ArrayList<>();
				addrs.add(serverAddress);
				MongoCredential credential = MongoCredential.createCredential(SpiderConfig.mongoUser, SpiderConfig.mongoDBName, SpiderConfig.mongoPassword.toCharArray());
				List<MongoCredential> credentials = new ArrayList<>();
				credentials.add(credential);
				//通过连接认证获取MongoDB连接
				mongoClient = new MongoClient(addrs, credentials);
				
				
				*//*ServerAddress serverAddress = new ServerAddress("106.14.62.40", 9666);
				List<ServerAddress> addrs = new ArrayList<>();
				addrs.add(serverAddress);
				MongoCredential credential = MongoCredential.createCredential("gduser_dev10", "guangdian", "Passw0rd&234$".toCharArray());
				List<MongoCredential> credentials = new ArrayList<>();
				credentials.add(credential);
				//通过连接认证获取MongoDB连接
				mongoClient = new MongoClient(addrs, credentials);*//*
			}
		} catch (MongoException e) {  
			e.printStackTrace();
		}*/
    }
	
	public static void main(String[] args) {
		
	}
	
}