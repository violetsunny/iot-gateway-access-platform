package com.ennew.iot.gateway.processor.job;

import com.ennew.iot.gateway.biz.server.cluster.ClusterManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClusterAliveKeeper implements Runnable{
    private final ClusterManager clusterManager;
    private final long initialDelay;
    private final long period;
    public ClusterAliveKeeper(ClusterManager clusterManager,long initialDelay,long period){
        this.clusterManager=clusterManager;
        this.initialDelay=initialDelay;
        this.period=period;
    }
    public void startup(){
        ScheduledExecutorService checkAliveTask = new ScheduledThreadPoolExecutor(1);
        //第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        checkAliveTask.scheduleAtFixedRate(this, initialDelay, period, TimeUnit.MILLISECONDS);
    }
    @Override
    public void run() {
        //开启定时调用checkAlive()
        try {
            clusterManager.checkAlive();
        } catch (Exception e) {
            log.warn("cluster keep alive failed");
        }
    }
}
