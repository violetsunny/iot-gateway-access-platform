package com.ennew.iot.gateway.biz.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@EnableScheduling
public class PushGatewayManager {

    @Value("${prometheus.pushgateway.url:10.39.64.13:9091}")
    private String pushGatewayUrl;

    @Value("${spring.application.name}")
    private String appId;

    @Value("${spring.profiles.active}")
    private String env;

    private PushGateway pushGateway;

    private Map<String, String> groupingKey;

    private CollectorRegistry registry;

    //@PostConstruct
    private void pushGatewayInit() {
        pushGateway = new PushGateway(pushGatewayUrl);
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        }catch (Exception e){
            ip = "";
        }
        String node = ip;
        groupingKey = new HashMap<String, String>(){{put("instance", node);put("env", env);}};
        registry = MetricCollector.getRegistry();
    }

    //@Scheduled(fixedRate = 5000)
    private void pushAllByTime(){
        pushAdd(registry);
    }

    public void pushAdd(CollectorRegistry registry)  {
        try {
            pushGateway.pushAdd(registry,appId,groupingKey);
        } catch (Exception e){
            log.warn("pushgateway 推送失败: {}",e);
        }

    }

    public void pushAdd(Collector collector)  {
        try {
            pushGateway.pushAdd(collector,appId,groupingKey);
        } catch (Exception e){
            log.warn("pushgateway 推送失败: {}",e);
        }
    }
}
