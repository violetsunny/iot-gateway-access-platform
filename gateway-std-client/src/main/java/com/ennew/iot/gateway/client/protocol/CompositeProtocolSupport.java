package com.ennew.iot.gateway.client.protocol;

import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.message.codec.Transport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class CompositeProtocolSupport implements ProtocolSupport {

    private String id;

    private String name;

    private String description;

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, DeviceMessageCodec> messageCodecSupports = new ConcurrentHashMap<>();

    public void addMessageCodecSupport(DeviceMessageCodec codec) {
        addMessageCodecSupport(codec.getSupportTransport(), codec);
    }

    public void addMessageCodecSupport(Transport transport, DeviceMessageCodec codec) {
        messageCodecSupports.put(transport.getId(), codec);
    }

    @Override
    public void close() {
        messageCodecSupports.clear();
    }

    @Override
    public DeviceMessageCodec getMessageCodec(Transport transport) {
        return messageCodecSupports.get(transport.getId());
    }
}
