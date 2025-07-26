package com.enn.iot.dtu.timer.cofig;

import com.enn.iot.dtu.timer.job.*;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Description: 作业任务调度器
 *
 * @author lixiangk
 * @version 1.0 2021/11/23
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    private final RefreshMainDataJob refreshMainDataJob;
    private final CheckMainDataJob checkMainDataJob;
    private final Metric2KafkaJob metric2KafkaJob;
    private final CallGcForResourceLeakDetectorJob callGcForResourceLeakDetectorJob;
    private final CmdDownJob cmdDownJob;

    public SchedulingConfig(RefreshMainDataJob refreshMainDataJob, CheckMainDataJob checkMainDataJob,
                            Metric2KafkaJob metric2KafkaJob, CallGcForResourceLeakDetectorJob callGcForResourceLeakDetectorJob,
                            CmdDownJob cmdDownJob) {
        this.refreshMainDataJob = refreshMainDataJob;
        this.checkMainDataJob = checkMainDataJob;
        this.metric2KafkaJob = metric2KafkaJob;
        this.callGcForResourceLeakDetectorJob = callGcForResourceLeakDetectorJob;
        this.cmdDownJob = cmdDownJob;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setThreadFactory(new DefaultThreadFactory("iot-job"));
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }


    /**
     * 主数据加载更新作业
     */
    @Scheduled(initialDelay = 60_000, fixedRate = 60_000)
    public void refreshMainDataJob() {
        long beginTime = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug("[JOB] refreshMainDataJob: 开始执行");
        }
        try {
            refreshMainDataJob.run();
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            if (log.isInfoEnabled()) {
                log.info("[JOB] refreshMainDataJob: 执行成功！cost: {}ms", cost);
            }
        } catch (Exception e) {
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            log.error("[JOB] refreshMainDataJob: 执行失败！cost: " + cost + "ms", e);
        }
    }

    /**
     * 监控指标发送作业, 执行周期：1分钟
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void metric2KafkaJob() {
        long beginTime = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug("[JOB] metric2KafkaJob: 开始执行");
        }
        try {
            metric2KafkaJob.run();
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            if (log.isInfoEnabled()) {
                log.info("[JOB] metric2KafkaJob: 执行成功！cost: {}ms", cost);
            }
        } catch (Exception e) {
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            log.error("[JOB] metric2KafkaJob: 执行失败！cost: " + cost + "ms", e);
        }
    }

    /**
     * 数据检查作业, 执行周期：30分钟
     */
    @Scheduled(initialDelay = 90_000, fixedRate = 30 * 60 * 1000)
    public void checkMainDataJob() {
        long beginTime = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug("[JOB] checkMainDataJob: 开始执行");
        }
        try {
            checkMainDataJob.run();
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            if (log.isInfoEnabled()) {
                log.info("[JOB] checkMainDataJob: 执行成功！cost: {}ms", cost);
            }
        } catch (Exception e) {
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginTime);
            log.error("[JOB] checkMainDataJob: 执行失败！cost: " + cost + "ms", e);
        }
    }


}
