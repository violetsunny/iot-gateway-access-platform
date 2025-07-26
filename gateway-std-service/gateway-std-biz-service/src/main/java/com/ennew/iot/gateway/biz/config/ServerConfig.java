package com.ennew.iot.gateway.biz.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerConfig {
    String ip;
    int port;
    String protocol;
    String protocolPath;
}
