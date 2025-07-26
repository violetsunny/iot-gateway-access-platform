package com.ennew.iot.gateway.client.message.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum DefaultTransport implements Transport {
    TCP("TCP"),
    HTTP("HTTP"),
    ;

    @Getter
    private final String name;

    static {
        Transports.register(Arrays.asList(DefaultTransport.values()));
    }

    @Override
    public String getId() {
        return name();
    }
}
