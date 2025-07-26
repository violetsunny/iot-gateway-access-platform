package com.ennew.iot.gateway.biz.concurrent;

import lombok.extern.slf4j.Slf4j;
import top.kdla.framework.common.help.ThreadPoolHelp;

import java.util.concurrent.*;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 16:05:37
 */
@Slf4j
public class FutureTaskScheduler extends Thread {
    private final ConcurrentLinkedQueue<Runnable> executeTaskQueue =
            new ConcurrentLinkedQueue<Runnable>();// 任务队列
    private final long sleepTime = 200;// 线程休眠时间
    private final ExecutorService pool = threadPoolHelp().getExecutorService();

    public static ThreadPoolHelp threadPoolHelp(){
        return ThreadPoolHelp.builder()
                .corePoolSize(4)
                .maximumPoolSize(40)
                .keepAliveTime(0L)
                .unit(TimeUnit.SECONDS)
                .workQueue(new LinkedBlockingDeque<>())
                .handler(new ThreadPoolExecutor.AbortPolicy())
                .factoryName("FutureTaskScheduler")
                .build();
    }
    private static final FutureTaskScheduler inst = new FutureTaskScheduler();

    private FutureTaskScheduler() {
        this.start();
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */


    public static void add(Runnable executeTask) {
        inst.executeTaskQueue.add(executeTask);
    }

    @Override
    public void run() {
        while (true) {
            handleTask();// 处理任务
            threadSleep(sleepTime);
        }
    }

    private void threadSleep(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            log.error("线程等待异常:", e);
        }
    }

    /**
     * 处理任务队列，检查其中是否有任务
     */
    private void handleTask() {
        Runnable executeTask;
        while (executeTaskQueue.peek() != null) {
            executeTask = executeTaskQueue.poll();
            handleTask(executeTask);
        }
    }

    /**
     * 执行任务操作
     *
     * @param executeTask
     */
    private void handleTask(Runnable executeTask) {
        pool.execute(new ExecuteRunnable(executeTask));
    }

    class ExecuteRunnable implements Runnable {
        Runnable executeTask;

        ExecuteRunnable(Runnable executeTask) {
            this.executeTask = executeTask;
        }

        public void run() {
            executeTask.run();
        }
    }
}
