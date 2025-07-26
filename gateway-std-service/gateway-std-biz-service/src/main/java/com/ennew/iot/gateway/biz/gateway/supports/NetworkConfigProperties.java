package com.ennew.iot.gateway.biz.gateway.supports;

import lombok.Data;

import java.util.Map;

@Data
public class NetworkConfigProperties {
    private String id;

    private String name;

    private String type;

    private Map<String,Object> configuration;

    private String description;
}
