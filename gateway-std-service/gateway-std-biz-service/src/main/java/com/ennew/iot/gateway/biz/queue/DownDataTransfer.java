package com.ennew.iot.gateway.biz.queue;

import com.ennew.iot.gateway.biz.concurrent.ThreadFactoryImpl;
import com.ennew.iot.gateway.client.protocol.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 下行消息队列
 *
 * @author hanyilong@enn.cn
 * @since 2021-02-08 16:09:03
 */
@Slf4j
public class DownDataTransfer {

    /**
     * 线程池
     */
//    private final int poolSize;
//
//    private final ExecutorService executorService;
//
//    /**
//     * 下行消息缓冲
//     */
//    private final BlockingQueue<Message> down2GateQueue;

//    public DownDataTransfer(BlockingQueue<Message> queue, int poolSize) {
//        this.poolSize = poolSize;
//        this.down2GateQueue = queue;
//        executorService = Executors.newFixedThreadPool(poolSize, new ThreadFactoryImpl("downDataTransfer_T", false));
//    }

//    @Override
//    public void run() {
//        for (int i = 0; i < poolSize; i++) {
//            executorService.execute(() -> {
//                while (true) {
//                    Message message = null;
//                    try {
//                        message = down2GateQueue.take();
//                        if (message == null) {
//                            continue;
//                        }
//                        // TODO 收到下行消息
//
//                        log.info("收到下行消息：{}", message);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    }
//
//    public void start() throws Exception {
//        new Thread(this).start();
//    }
}
