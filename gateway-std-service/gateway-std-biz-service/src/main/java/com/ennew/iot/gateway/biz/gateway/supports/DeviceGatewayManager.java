package com.ennew.iot.gateway.biz.gateway.supports;

import com.ennew.iot.gateway.biz.gateway.DeviceGateway;

public interface DeviceGatewayManager {

    DeviceGateway getGateway(String id);

    void removeGatewayCache(String id);
}
