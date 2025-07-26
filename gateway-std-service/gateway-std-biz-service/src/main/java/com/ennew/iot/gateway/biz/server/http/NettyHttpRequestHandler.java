package com.ennew.iot.gateway.biz.server.http;

/**
 * Netty HTTP 请求处理器
 */
public interface NettyHttpRequestHandler {



    void handle(NettyHttpRequest request, NettyHttpResponse response);
}
