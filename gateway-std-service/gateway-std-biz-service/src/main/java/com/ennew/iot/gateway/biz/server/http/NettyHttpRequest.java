package com.ennew.iot.gateway.biz.server.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.nio.charset.StandardCharsets;


/**
 * Netty Http 请求
 */
public class NettyHttpRequest {

    private final FullHttpRequest fullHttpRequest;


    public NettyHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }


    public String uri(){
        return fullHttpRequest.uri();
    }

    public HttpMethod method(){
        return fullHttpRequest.method();
    }

    public String getHeader(String key){
        return fullHttpRequest.headers().get(key);
    }

    public HttpHeaders headers(){
        return fullHttpRequest.headers();
    }

    public String content(){
        return fullHttpRequest.content().toString(StandardCharsets.UTF_8);
    }
}
