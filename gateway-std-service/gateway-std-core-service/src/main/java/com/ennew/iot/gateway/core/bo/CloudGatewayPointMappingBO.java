package com.ennew.iot.gateway.core.bo;

import lombok.Data;

@Data
public class CloudGatewayPointMappingBO {


    private Long pointId;


    private String deviceId;


    private String metric;


    private String productId;
}
