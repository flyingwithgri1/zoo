package com.spider.scheduler;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.spider.bean.Request;


public class BloomFilterDuplicateChecker implements DuplicateChecker {

	protected Logger logger = Logger.getLogger(getClass());
	
	private final BloomFilter<CharSequence> bloomFilter;
	
	private int expectedInsertions;

	private double fpp;

	private AtomicInteger counter;

	public BloomFilterDuplicateChecker(int expectedInsertions) {
		this(expectedInsertions, 0.00001);
	}

	public BloomFilterDuplicateChecker(int expectedInsertions, double fpp) {
		this.expectedInsertions = expectedInsertions;
		this.fpp = fpp;
		this.bloomFilter = rebuildBloomFilter();
	}

	protected BloomFilter<CharSequence> rebuildBloomFilter() {
		counter = new AtomicInteger(0);
		return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
	}

	public synchronized boolean isDuplicate(Request request) {
		boolean isDuplicate = false;
		try {
			String dedupUrlStr = getDedupUrlString(request);
			isDuplicate = bloomFilter.mightContain(dedupUrlStr);
			if (isDuplicate) {
				return isDuplicate;
			} else {
				bloomFilter.put(dedupUrlStr);
				counter.incrementAndGet();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isDuplicate;
	}

	protected String getDedupUrlString(Request request) {
		return request.getUrl();
	}
	
	public void resetDuplicateChecker() {
		rebuildBloomFilter();
	}

	public int getTotalRequestsCount() {
		return counter.get();
	}
}
