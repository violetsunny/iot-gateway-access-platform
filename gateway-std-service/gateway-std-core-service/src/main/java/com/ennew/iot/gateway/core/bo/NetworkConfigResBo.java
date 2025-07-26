package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import java.util.Map;

@Data
public class NetworkConfigResBo {

    private String id;

    private String name;

    private String type;

    private Map<String,Object> configuration;

    private String description;
}
