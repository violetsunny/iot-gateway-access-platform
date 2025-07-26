package com.ennew.iot.gateway.client.message.codec;

public interface Transport {

    String getId();

    default String getName() {
        return getId();
    }
}
