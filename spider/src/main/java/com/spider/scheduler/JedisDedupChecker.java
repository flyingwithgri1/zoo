package com.spider.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.spider.bean.Request;
import com.spider.utils.JedisUtil;
import com.spider.utils.SpiderConfig;

import redis.clients.jedis.Jedis;

public class JedisDedupChecker implements DuplicateChecker{

	protected Logger logger = Logger.getLogger(getClass());
	
	private String dedup = SpiderConfig.redisDedup;
	
	private int index = SpiderConfig.redisSelect;
	
	private AtomicInteger counter;
	
	public JedisDedupChecker() {
		counter = new AtomicInteger(0);
	}
	
	public JedisDedupChecker(String dedup, int index){
		this.dedup = dedup;
		this.index = index;
	}
	
	@Override
	public boolean isDuplicate(Request request) {
		Jedis jedis = null;
		try{
			jedis = JedisUtil.getResource();
			jedis.select(index);
			if(jedis.hexists(dedup, request.getUrl())){
				return true;
			}else{
				jedis.hset(dedup, request.getUrl(),"");
				counter.incrementAndGet();
			}
		}catch (Exception e) {
			logger.error("redis duplicate error",e);
		}finally {
			JedisUtil.close(jedis);
		}
		return false;
	}

	@Override
	public void resetDuplicateChecker() {
		counter = new AtomicInteger(0);
	}

	@Override
	public int getTotalRequestsCount() {
		return counter.get();
	}

	public String getDedup() {
		return dedup;
	}

	public void setDedup(String dedup) {
		this.dedup = dedup;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
