package com.ennew.iot.gateway.biz.gateway;

import com.ennew.iot.gateway.biz.gateway.supports.DeviceGatewayProperties;

public interface DeviceGatewayProvider {

    String getId();

    DeviceGateway createDeviceGateway(DeviceGatewayProperties properties);
}
