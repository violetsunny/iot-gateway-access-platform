package com.ennew.iot.gateway.biz.concurrent;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import top.kdla.framework.common.help.ThreadPoolHelp;

import java.util.concurrent.*;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 18:51:05
 */
@Slf4j
public class CallbackTaskScheduler extends Thread {
    private final ConcurrentLinkedQueue<CallbackTask> executeTaskQueue =
            new ConcurrentLinkedQueue<CallbackTask>();// 任务队列
    private final long sleepTime = 200;// 线程休眠时间
    private final ExecutorService jPool = threadPoolHelp().getExecutorService();

    public static ThreadPoolHelp threadPoolHelp(){
        return ThreadPoolHelp.builder()
                .corePoolSize(4)
                .maximumPoolSize(40)
                .keepAliveTime(0L)
                .unit(TimeUnit.SECONDS)
                .workQueue(new LinkedBlockingDeque<>())
                .handler(new ThreadPoolExecutor.AbortPolicy())
                .factoryName("CallbackTaskScheduler")
                .build();
    }

    ListeningExecutorService gPool =
            MoreExecutors.listeningDecorator(jPool);


    private static final CallbackTaskScheduler inst = new CallbackTaskScheduler();

    private CallbackTaskScheduler() {
        this.start();
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */


    public static <R> void add(CallbackTask<R> executeTask) {
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

        CallbackTask executeTask = null;
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
    private <R> void handleTask(CallbackTask<R> executeTask) {

        ListenableFuture<R> future = gPool.submit(() -> {
            R r = executeTask.execute();
            return r;
        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        }, gPool);


    }

}
