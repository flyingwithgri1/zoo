package com.spider.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.spider.bean.Request;
import com.spider.utils.JedisUtil;
import com.spider.utils.SpiderConfig;

import redis.clients.jedis.Jedis;

/**
 * 待抓取队列，基于redis
 * */
public class JedisScheduler extends DedupScheduler{

	private String noPriorityQueue = SpiderConfig.redisNoPriorityQueue;
	    
	private String priorityQueuePlus = SpiderConfig.redispriorityQueuePlus;
	 
	private String priorityQueueMinus = SpiderConfig.redispriorityQueueMinus;

	private int index = SpiderConfig.redisSelect;
	
	@Override
	public synchronized Request poll() {
		Jedis jedis = null;
		try{
			jedis = JedisUtil.getResource();
			jedis.select(index);
			String text = jedis.lpop(priorityQueuePlus);
			if(text != null){
				return JSONObject.toJavaObject(JSONObject.parseObject(text), Request.class);
			}
			text = jedis.lpop(noPriorityQueue);
			if(text != null){
				return JSONObject.toJavaObject(JSONObject.parseObject(text), Request.class);
			}
			text = jedis.lpop(priorityQueueMinus);
			if(text != null){
				return JSONObject.toJavaObject(JSONObject.parseObject(text), Request.class);
			}
		}catch (Exception e) {
		}finally{
			if(jedis != null){
				JedisUtil.close(jedis);
			}
		}
		
		return null;
	}

	@Override
	public void pushWhenNoDuplicate(Request request) {
		if (request.getPriority() == 0) {
			JedisUtil.rpush(noPriorityQueue, JSONObject.toJSON(request).toString(), index);
        } else if (request.getPriority() > 0) {
            JedisUtil.rpush(priorityQueuePlus, JSONObject.toJSON(request).toString(), index);
        } else {
        	JedisUtil.rpush(priorityQueueMinus, JSONObject.toJSON(request).toString(), index);
        }
	}

}
