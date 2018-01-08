package com.spider.scheduler;

import com.spider.bean.Request;

public interface DuplicateChecker {
    public boolean isDuplicate(Request request);

    public void resetDuplicateChecker();

    public int getTotalRequestsCount();

}
