package com.ennew.iot.gateway.biz.prometheus;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.SimpleCollector;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MetricCollector {

    /** 测点注册中心
     * 统一注册组件内的所有测点 以统一上报
     */
    @Getter
    private static final CollectorRegistry registry = new CollectorRegistry();

    /** http上报计数器
     * http上报次数 rtg/history/info/status/event
     */
    public static Counter httpUpCounter = Counter.build("gateway_http_up_count_total","http网关上报次数").labelNames("type").register(registry);

    /** tcp上报计数器
     * tcp上报次数
     */
    public static Counter tcpUpCounter = Counter.build("gateway_tcp_up_count_total","tcp网关上报次数").labelNames("type").register(registry);

    /** feign外部接口调用计数器
     * 统计不同feign url接口的调用次数
     */
    public static Counter feignCounter = Counter.build("gateway_std_feign_count_total","gateway-std feign调用次数").labelNames("url").register(registry);
}
