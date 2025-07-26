package com.ennew.iot.gateway.biz.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class DataThreadRejectPolicy implements RejectedExecutionHandler {


    private String poolName;

    public DataThreadRejectPolicy(String poolName) {

        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        // 自定义拒绝策略的处理逻辑
        log.warn("ThreadPool {} Task Rejected Warning: {}", poolName, runnable.toString());
        if (!executor.isShutdown()) {
            runnable.run();
        }
    }

}
