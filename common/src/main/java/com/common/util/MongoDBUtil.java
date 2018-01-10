package com.common.util;

import java.util.ArrayList;
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
	private MongoClient mongoClient = null;
	private String user;
	private String passwd;
	private String host;
	private int port;
	private String dbName;

	public MongoCollection<Document> getDBCollection(String dbName,String collName) {
		return mongoClient.getDatabase(dbName).getCollection(collName);
	}

	 /** 
	 * 初始化连接池 
	 */  
	private void init() {
		try {
			if(StringUtils.isBlank(user) || StringUtils.isBlank(passwd)){
				mongoClient = new MongoClient(host, port);
			}else{
				ServerAddress serverAddress = new ServerAddress(host, port);
				List<ServerAddress> addrs = new ArrayList<ServerAddress>();
				addrs.add(serverAddress);
				MongoCredential credential = MongoCredential.createCredential(user, dbName, passwd.toCharArray());
				List<MongoCredential> credentials = new ArrayList<MongoCredential>();
				credentials.add(credential);
				//通过连接认证获取MongoDB连接
				mongoClient = new MongoClient(addrs, credentials);
			}
		} catch (MongoException e) {  
			e.printStackTrace();
		}
    }

	public void setUser(String user) {
		this.user = user;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}