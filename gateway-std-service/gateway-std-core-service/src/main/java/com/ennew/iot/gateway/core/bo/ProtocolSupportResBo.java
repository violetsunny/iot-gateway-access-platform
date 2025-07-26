package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.util.Map;

@Data
public class ProtocolSupportResBo {

    private String id;

    private String name;

    private String description;

    private String type;

    private Integer way;

    private Byte state;

    private Byte isTemplate;

    private Map<String, Object> configuration;

}
