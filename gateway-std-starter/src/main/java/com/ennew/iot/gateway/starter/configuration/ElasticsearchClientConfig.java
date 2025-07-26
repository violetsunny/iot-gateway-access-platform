//package com.ennew.iot.gateway.starter.configuration;///**
//
//
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//
///**
// * es配置
// *
// * @author kanglele
// * @version $Id: IotClientConfig, v 0.1 2023/1/11 17:40 kanglele Exp $
// */
//@Configuration
//public class ElasticsearchClientConfig extends ElasticsearchConfiguration {
//
//    @Value("${elasticsearch.client.host:127.0.0.1}")
//    private String host;
//    @Value("${elasticsearch.client.port:9200}")
//    private String port;
//    @Value("${elasticsearch.client.connectTimeout:5}")
//    private Long connectTimeout;
//    @Value("${elasticsearch.client.socketTimeout:3}")
//    private Long socketTimeout;
//
//    /**
//     * 可以使用 ElasticsearchOperations或ElasticsearchTemplate
//     * @return
//     */
//    @NotNull
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(host+":"+port)
//                .withConnectTimeout(connectTimeout)
//                .withSocketTimeout(socketTimeout)
//                .build();
//    }
//}
