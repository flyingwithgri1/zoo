package com.spider.scheduler;

import com.spider.bean.Request;

public class SchedulerManager implements Scheduler{

	/*
	 * redis队列
	 * */
	private Scheduler jedisScheduler = new JedisScheduler();
	
	/*
	 * 本地队列
	 * */
	private Scheduler priorityScheduler = new PriorityScheduler();
	
	@Override
	public void push(Request request) {
		if("redis".equals(request.getQueuepath())){
			jedisScheduler.push(request);
		}else{
			priorityScheduler.push(request);
		}
	}

	/**
	 * 优先选取本地队列
	 * */
	@Override
	public Request poll() {
		Request request = priorityScheduler.poll();
		if(request == null){
			request = jedisScheduler.poll();
		}
		return request;
	}

}
