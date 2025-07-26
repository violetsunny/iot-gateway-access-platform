package com.ennew.iot.gateway.client.protocol;

import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.message.codec.Transport;

import java.io.Closeable;

public interface ProtocolSupport extends Closeable {

    String getId();

    String getName();

    String getDescription();

    DeviceMessageCodec getMessageCodec(Transport transport);
}
