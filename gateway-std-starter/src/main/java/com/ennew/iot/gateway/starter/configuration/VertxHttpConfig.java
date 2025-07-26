/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.starter.configuration;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;

/**
 * @author kanglele
 * @version $Id: VertxHttpConfig, v 0.1 2023/5/18 10:22 kanglele Exp $
 */
@Configuration
public class VertxHttpConfig {

    @Value("${http.connectionPoolSize:1000}")
    private int connectionPoolSize;

    @Value("${http.connectTimeout:1000}")
    private int connectTimeout;

    @Value("${http.idleTimeout:10}")
    private int idleTimeout;

    @Value("${http.maxWaitQueueSize:500}")
    private int maxWaitQueueSize;

//    @Bean
//    public Vertx vertx() {
//        return Vertx.vertx();
//    }
//
//    @Bean
//    public WebClient webClient() {
//        return WebClient.create(vertx(), new WebClientOptions()
////                .setSsl(true)
////                .setTrustAll(true)
//                .setProtocolVersion(HttpVersion.HTTP_1_1)
//                .setKeepAlive(true)
//                .setMaxPoolSize(connectionPoolSize)
//                .setConnectTimeout(connectTimeout)
//                .setIdleTimeout(idleTimeout)
//                .setMaxWaitQueueSize(maxWaitQueueSize));
//    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        // httpClient创建器
        clientHttpRequestFactory.setHttpClient(httpClient);
        // 连接超时时间/毫秒（连接上服务器(握手成功)的时间，超出抛出connect timeout）
        clientHttpRequestFactory.setConnectTimeout(connectTimeout);
        // 数据读取超时时间(socketTimeout)/毫秒（务器返回数据(response)的时间，超过抛出read timeout）
        clientHttpRequestFactory.setReadTimeout(10 * 1000);
        // 连接池获取请求连接的超时时间，不宜过长，必须设置/毫秒（超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool）
        clientHttpRequestFactory.setConnectionRequestTimeout(10 * 1000);
        return clientHttpRequestFactory;
    }
    @Bean
    public RestTemplate restTemplate (ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        // 配置请求工厂
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.getMessageConverters().add(new HttpMessageConverter());
        return restTemplate;
    }


//    @PreDestroy
//    public void close() {
//        webClient().close();
//        vertx().close();
//    }

}
