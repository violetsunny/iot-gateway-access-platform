package com.ennew.iot.gateway.biz.gateway.tcp;

import java.util.HashMap;
import java.util.Map;

public class TcpServerProperties {

    private String id;

//    private NetServerOptions options;
//
//    private PayloadType payloadType;
//
//    private PayloadParserType parserType;

//    private Map<String, Object> parserConfiguration = new HashMap<>();

    private String host;

    private int port;

    private boolean ssl;

    //服务实例数量(线程数)
    private final int instance = Runtime.getRuntime().availableProcessors();

    private String certId;
}
