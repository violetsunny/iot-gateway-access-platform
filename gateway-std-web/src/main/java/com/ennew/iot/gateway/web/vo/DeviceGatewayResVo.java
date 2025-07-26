package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "DeviceGatewayResVo对象")
@Data
public class DeviceGatewayResVo {
    private String id;

    private String name;

    private String type;

    private String protocol;

    private String protocolName;

    private NetworkConfigState state;

    private Map<String,Object> configuration;

    private Map<String, Object> networkConfiguration;

    private String networkId;

    private String description;
}
