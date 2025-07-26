package com.ennew.iot.gateway.biz.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-15 10:58:03
 */
public class ThreadFactoryImpl implements ThreadFactory {
    AtomicInteger index = new AtomicInteger(0);
    private final String threadNamePrefix;
    private final boolean isDaemon;

    public ThreadFactoryImpl(String threadNamePrefix, boolean isDaemon) {
        super();
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
    }


    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, threadNamePrefix + index.addAndGet(1));
        thread.setDaemon(isDaemon);
        return thread;
    }
}
