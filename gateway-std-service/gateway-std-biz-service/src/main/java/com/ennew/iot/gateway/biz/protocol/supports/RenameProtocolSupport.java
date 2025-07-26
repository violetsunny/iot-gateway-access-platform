package com.ennew.iot.gateway.biz.protocol.supports;

import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.message.codec.Transport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

@AllArgsConstructor
public class RenameProtocolSupport implements ProtocolSupport {

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final String description;

    private final ProtocolSupport target;

    @Override
    @SneakyThrows
    public void close() {
        target.close();
    }

    @Override
    public DeviceMessageCodec getMessageCodec(Transport transport) {
        return target.getMessageCodec(transport);
    }
}
