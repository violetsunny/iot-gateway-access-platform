package com.ennew.iot.gateway.core.bo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.Data;

import java.util.Map;

@Data
public class DeviceGatewayBo {
    private String id;

    private String name;

    private String type;

    private String protocol;

    private NetworkConfigState state;

    private Map<String,Object> configuration;

    private String networkId;

    private String description;
}
