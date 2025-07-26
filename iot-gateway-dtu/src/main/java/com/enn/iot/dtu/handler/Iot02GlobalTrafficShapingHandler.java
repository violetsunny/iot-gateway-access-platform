package com.enn.iot.dtu.handler;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class Iot02GlobalTrafficShapingHandler extends GlobalTrafficShapingHandler {

    public Iot02GlobalTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
        super(executor, checkInterval);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        log.info("[02]流量：接收速率: {} B/s, 发送速率: {} B/s", counter.lastReadThroughput(), counter.lastWriteThroughput());
    }
}
