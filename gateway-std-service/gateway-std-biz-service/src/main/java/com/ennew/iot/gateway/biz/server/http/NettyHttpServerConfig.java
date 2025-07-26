package com.ennew.iot.gateway.biz.server.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Netty HTTP Server SpringBoot 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ennew.httpserver")
public class NettyHttpServerConfig {


    /**
     * Netty HTTP Server 配置
     */
    @NestedConfigurationProperty
    private NettyHttpServerOptions options;


    /**
     * 是否启用 Netty HTTP Server 组件
     */
    private boolean enabled = false;
}
