package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.properties.IotProperties;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateHandler;

public class Iot03IdleStateHandler extends ChannelDuplexHandler {
    private final IdleStateHandler idleStateHandler;

    public Iot03IdleStateHandler(IotProperties iotProperties) {
        idleStateHandler = new IdleStateHandler((int)iotProperties.getReaderIdleTimeout().getSeconds(),
                (int)iotProperties.getWriterIdleTimeout().getSeconds(),
                (int)iotProperties.getAllIdleTimeout().getSeconds());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.handlerRemoved(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        idleStateHandler.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        idleStateHandler.channelReadComplete(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        idleStateHandler.write(ctx, msg, promise);
    }
}
