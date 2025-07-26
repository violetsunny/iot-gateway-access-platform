package com.ennew.iot.gateway.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "emqx")
@Data
public class EmqxConfiguration {
    String host;
    int port;
    boolean TLS;
    String userName;
    String password;
}

