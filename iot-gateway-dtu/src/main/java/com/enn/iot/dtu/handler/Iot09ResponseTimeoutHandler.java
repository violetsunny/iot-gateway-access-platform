package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.event.IotResponseTimeoutEvent;
import com.enn.iot.dtu.common.properties.IotProperties;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Iot09ResponseTimeoutHandler extends MessageToMessageCodec<IotCmdResp, AbstractIotCmdReq> {

    private final long responseTimeoutMs;

    /**
     * 前一条指令请求发送时间
     */
    private long lastWriteTime = -1;
    /**
     * 前一次指令应答接收时间
     */
    private long lastReadTime = -1;
    /**
     * 调度任务句柄
     */
    private ScheduledFuture<?> taskFuture;

    public Iot09ResponseTimeoutHandler(IotProperties iotProperties) {
        this.responseTimeoutMs = iotProperties.getResponseTimeout().toMillis();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        destroyTask();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        destroyTask();
        super.channelInactive(ctx);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, IotCmdResp msg, List<Object> out) {
        this.lastReadTime = System.currentTimeMillis();
        destroyTask();
        out.add(msg);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractIotCmdReq msg, List<Object> out) {
        this.lastWriteTime = System.currentTimeMillis();
        scheduleTask(ctx);
        out.add(msg);
    }

    private void scheduleTask(ChannelHandlerContext ctx) {
        if (responseTimeoutMs > 0) {
            destroyTask();
            taskFuture =
                    ctx.executor().schedule(new IotResponseTimeoutTask(ctx), responseTimeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    private void destroyTask() {
        if (taskFuture != null) {
            taskFuture.cancel(true);
            taskFuture = null;
        }
    }

    private class IotResponseTimeoutTask implements Runnable {
        private final ChannelHandlerContext ctx;

        IotResponseTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (!ctx.channel().isOpen()) {
                return;
            }
            long curTime = System.currentTimeMillis();
            long afterWriteTime = curTime - lastWriteTime;
            boolean lastIsWrite = lastWriteTime > lastReadTime;
            boolean isTimeout = afterWriteTime >= responseTimeoutMs;
            // 触发响应超时
            if (lastIsWrite && isTimeout) {
                if (log.isTraceEnabled()) {
                    log.trace(
                            Log.context(ctx) + "[09] 指令应答超时！等待应答时间: {}ms, 超时配置: {}ms, 当前时间={}ms, 上次写时间={}ms, 上次读时间={}ms",
                            afterWriteTime, responseTimeoutMs, curTime, lastWriteTime, lastReadTime);
                }
                ctx.fireUserEventTriggered(IotResponseTimeoutEvent.instance(afterWriteTime, responseTimeoutMs, curTime,
                        lastWriteTime, lastReadTime));
            } else {
                if (log.isWarnEnabled()) {
                    log.warn(
                            Log.context(ctx) + "[09] 不满足断开连接条件！等待应答时间: {}ms, 超时配置: {}ms, 当前时间={}ms, 上次写时间={}ms, 上次读时间={}ms",
                            curTime, lastWriteTime, lastReadTime);
                }
            }
        }

    }
}

