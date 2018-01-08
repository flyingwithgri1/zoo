package com.spider.scheduler;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.http.annotation.ThreadSafe;

import com.spider.bean.Request;
import com.spider.utils.NumberUtils;

/**
 * 待抓取队列，基于内存
 * */
@ThreadSafe
public class PriorityScheduler extends DedupScheduler {

    public static final int INITIAL_CAPACITY = 5;

    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();
    
    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(
            INITIAL_CAPACITY, new Comparator<Request>() {
                public int compare(Request o1, Request o2) {
                    return -NumberUtils.compareInt(o1.getPriority(),
                            o2.getPriority());
                }
            });

    private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<Request>(
            INITIAL_CAPACITY, new Comparator<Request>() {
                public int compare(Request o1, Request o2) {
                    return NumberUtils.compareInt(o1.getPriority(),
                            o2.getPriority());
                }
            });

    public void pushWhenNoDuplicate(Request request) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    public synchronized Request poll() {
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

}
