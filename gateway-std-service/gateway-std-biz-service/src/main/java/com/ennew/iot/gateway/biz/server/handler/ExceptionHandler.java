package com.ennew.iot.gateway.biz.server.handler;

import com.ennew.iot.gateway.biz.session.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private SessionManger sessionManger;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(this.getClass().getName() + " 异常处理, e:" + cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        sessionManger.closeSession(ctx);
    }
}