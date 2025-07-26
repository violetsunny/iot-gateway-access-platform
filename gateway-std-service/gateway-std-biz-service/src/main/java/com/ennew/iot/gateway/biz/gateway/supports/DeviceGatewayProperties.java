package com.ennew.iot.gateway.biz.gateway.supports;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.Data;

import java.util.Map;

@Data
public class DeviceGatewayProperties {
    private String id;

    private String name;

    private String type;

    private String protocol;

    private String protocolName;

    private NetworkConfigState state;

    private Map<String,Object> configuration;

    private NetworkConfigProperties networkConfigProperties;

    private String description;
}
