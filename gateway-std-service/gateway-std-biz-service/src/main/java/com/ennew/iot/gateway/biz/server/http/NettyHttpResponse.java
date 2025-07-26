package com.ennew.iot.gateway.biz.server.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Netty Http 请求相应
 */
public class NettyHttpResponse {

    public static final String EMPTY_BODY = null;

    private final ChannelHandlerContext ctx;

    public NettyHttpResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }


    public void send(HttpResponseStatus status, String body, boolean closeAfterSend, Consumer<Boolean> sendResultConsumer) {
        DefaultFullHttpResponse response;
        if (body == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        } else {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(bytes));
        }
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (sendResultConsumer != null) {
            channelFuture.addListener((ChannelFutureListener) cf -> sendResultConsumer.accept(cf.isSuccess()));
        }
        if (closeAfterSend) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    public void send(HttpResponseStatus status, String body) {
        send(status, body, true, null);
    }

    public void sendOk(String body) {
        send(HttpResponseStatus.OK, body, true, null);
    }
}
