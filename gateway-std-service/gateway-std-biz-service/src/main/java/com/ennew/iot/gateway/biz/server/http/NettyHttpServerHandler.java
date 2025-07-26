package com.ennew.iot.gateway.biz.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;

import static com.ennew.iot.gateway.biz.server.http.NettyHttpServer.COMMON_ROUTE;
import static com.ennew.iot.gateway.biz.server.http.NettyHttpServer.METHOD_URI_ROUTE_FORMAT;


public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    private final Map<String, NettyHttpRequestHandler> routingMap;

    public NettyHttpServerHandler(Map<String, NettyHttpRequestHandler> routingMap) {
        this.routingMap = routingMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String method = request.method().name();
            String uri = request.uri();
            NettyHttpRequest nettyHttpRequest = new NettyHttpRequest(request);
            NettyHttpResponse nettyHttpResponse = new NettyHttpResponse(ctx);
            NettyHttpRequestHandler handler = routingMap.get(MessageFormat.format(METHOD_URI_ROUTE_FORMAT, method, uri));
            if(handler == null){
                handler = routingMap.get(uri);
            }
            if(handler == null){
                handler = routingMap.get(COMMON_ROUTE);
            }
            if(handler == null){
                nettyHttpResponse.send(HttpResponseStatus.NOT_FOUND, null);
                return;
            }
            handler.handle(nettyHttpRequest, nettyHttpResponse);
        }else{
            LOGGER.debug("unsupported http protocol");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("unexpected exception occurred", cause);
    }

}
