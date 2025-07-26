package com.ennew.iot.gateway.starter.configuration;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Iterator;
import java.util.function.BiConsumer;

@Configuration
@Slf4j
public class EsConfig {

    @Value("${spring.elasticsearch.high-level-client.ip}")
    private String ip;
    @Value("${spring.elasticsearch.high-level-client.port}")
    private String port;
    @Value("${spring.elasticsearch.high-level-client.username:-1}")
    private String user;
    @Value("${spring.elasticsearch.high-level-client.password:-1}")
    private String pwd;
    @Value("${spring.elasticsearch.high-level-client.scheme:http}")
    private String scheme;

    @Value("${spring.elasticsearch.high-level-client.actions:100}")
    private Integer actions;
    @Value("${spring.elasticsearch.high-level-client.size:5}")
    private Integer size;
    @Value("${spring.elasticsearch.high-level-client.wait-time:10}")
    private Integer timeWait;
    @Value("${spring.elasticsearch.high-level-client.retry-times:3}")
    private Integer retryTimes;

    /**
     * @return
     */
    @Bean("bulkProcessor")
    public BulkProcessor bulkProcessor() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (StringUtils.isNotBlank(user) && !user.equals("-1")) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
        }

        RestHighLevelClient restClient = null;
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(user, pwd));

        if ("https".equalsIgnoreCase(scheme)) {
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    // 信任所有
                    public boolean isTrusted(X509Certificate[] chain, String authType) {
                        return true;
                    }
                }).build();
                SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sslContext, NoopHostnameVerifier.INSTANCE);
                restClient = new RestHighLevelClient(
                        RestClient.builder(
                                        new HttpHost(ip, Integer.parseInt(port), scheme))
                                .setHttpClientConfigCallback(httpClientBuilder -> {
                                    httpClientBuilder.disableAuthCaching();
                                    httpClientBuilder.setKeepAliveStrategy((response, context) -> Duration.ofMinutes(5).toMillis());
                                    httpClientBuilder.setSSLStrategy(sessionStrategy);
                                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                    return httpClientBuilder;
                                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            restClient = new RestHighLevelClient(
                    RestClient.builder(
                                    new HttpHost(ip, Integer.parseInt(port), scheme))
                            .setHttpClientConfigCallback(httpClientBuilder -> {
                                httpClientBuilder.disableAuthCaching();
                                httpClientBuilder.setKeepAliveStrategy((response, context) -> Duration.ofMinutes(5).toMillis());
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            })
            );
        }

        RestHighLevelClient finalRestClient = restClient;
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                (request, bulkListener) -> {
                    request.timeout(TimeValue.timeValueSeconds(60));
                    finalRestClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
                };

        return BulkProcessor.builder(bulkConsumer, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                int i = request.numberOfActions();
                log.info("ES bulkProcessor 同步数量{}", i);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                Iterator<BulkItemResponse> iterator = response.iterator();
                while (iterator.hasNext()) {
                    BulkItemResponse bulkItemResponse = iterator.next();
                    //log.info("add device data:{}", JSON.toJSONString(bulkItemResponse));
                    if (bulkItemResponse.isFailed()) {
                        log.warn("add device data fail:{}", bulkItemResponse.getFailureMessage());
                    } else {
                        log.info("bulkProcessor add device data,sn:{},id:{}", bulkItemResponse.getIndex(), bulkItemResponse.getResponse().getId());
                    }
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("bulkProcessor {} data bulk failed,reason :{}", executionId, failure);
            }
        })
        //  达到刷新的条数
        .setBulkActions(actions)
        // 达到 刷新的大小
        .setBulkSize(new ByteSizeValue(size, ByteSizeUnit.MB))
        // 固定刷新的时间频率
        .setFlushInterval(TimeValue.timeValueSeconds(timeWait))
        //并发线程数
        .setConcurrentRequests(1)
        // 重试补偿策略
        .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), retryTimes))
        .build();

    }
}
