package com.ennew.iot.gateway.core.bo;

import lombok.Data;

@Data
public class CloudGatewayModbusConfigBO {


    private String host;


    private Integer port;


    private Integer slaveAddress;
}
