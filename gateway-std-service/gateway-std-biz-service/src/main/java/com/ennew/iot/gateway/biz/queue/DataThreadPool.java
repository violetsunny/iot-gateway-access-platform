package com.ennew.iot.gateway.biz.queue;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

public class DataThreadPool {




    private static class SingletonHolder {
        private static ExecutorService UP_DATA_THREAD_POOL = initExecutor(5, 2, "UP_DATA_POOL");
        private static ExecutorService DOWN_DATA_THREAD_POOL = initExecutor(1,1,"DOWN_DATA_POOL");
    }

    public static ExecutorService getUpExecutorService() {
        return SingletonHolder.UP_DATA_THREAD_POOL;
    }
    public static ExecutorService getDownExecutorService() {
        return SingletonHolder.DOWN_DATA_THREAD_POOL;
    }






    private static ExecutorService initExecutor(int corePoolSize, int multiple, String pooName) {

//        int corePoolSize = 10; // 核心线程数
        int maxPoolSize = corePoolSize * multiple; // 最大线程数
        long keepAliveTime = 60; // 线程空闲时间
        TimeUnit timeUnit = TimeUnit.SECONDS; // 时间单位
        int queueCapacity = 4096; // 任务队列容量

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                timeUnit,
                new LinkedBlockingQueue<>(queueCapacity)
        );

        executor.setThreadFactory(new CustomizableThreadFactory(pooName + '-'));

        // 可选：设置拒绝策略
        executor.setRejectedExecutionHandler(new DataThreadRejectPolicy(pooName));

        return executor;
    }

}
