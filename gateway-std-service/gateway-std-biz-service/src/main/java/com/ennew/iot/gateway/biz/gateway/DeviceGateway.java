package com.ennew.iot.gateway.biz.gateway;

import com.ennew.iot.gateway.client.message.codec.Transport;

public interface DeviceGateway {

    String getId();

    void startup();

    void shutdown();

    void pause();

    default boolean isStarted() {
        return getState() == GatewayState.started;
    }

    default GatewayState getState() {
        return GatewayState.started;
    }

    Transport getTransport();
}
