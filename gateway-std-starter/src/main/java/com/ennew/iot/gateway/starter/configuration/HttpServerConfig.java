package com.ennew.iot.gateway.starter.configuration;

import com.ennew.iot.gateway.biz.ctwing.CtwingHttp;
import com.ennew.iot.gateway.biz.server.http.NettyHttpServer;
import com.ennew.iot.gateway.biz.server.http.NettyHttpServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "ennew.httpserver.enabled", havingValue = "true")
public class HttpServerConfig {

    @Bean
    public NettyHttpServer nettyHttpServer(NettyHttpServerConfig config, CtwingHttp ctwingHttp){
        NettyHttpServer nettyHttpServer = new NettyHttpServer(config.getOptions());
        nettyHttpServer.addHandler("/ctwing/message-push", ctwingHttp);
        nettyHttpServer.startup();
        return nettyHttpServer;
    }


}
