package com.geektime.crawl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

public class ContinuousCrawlerThreadFactory implements ThreadFactory {

	private static AtomicLong counter = new AtomicLong(1L);
	private static String THREAD_PREFIX = "crawler";
	
	private Logger clientLog;
	
	public ContinuousCrawlerThreadFactory(final Logger clientLog) {
		this.clientLog = clientLog;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		final Thread t = new Thread(r);
		t.setDaemon(false); // we don't want the JVM to exit as long as a crawl is still running
		t.setName( String.format( "%s-%d", THREAD_PREFIX, counter.getAndIncrement() ) );
		t.setUncaughtExceptionHandler( (t1, e) -> clientLog.error( String.format( "uncaught exception in crawler thread [%s]", t1 ), e) );
		return t;
	}

}
