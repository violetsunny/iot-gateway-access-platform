package com.ennew.iot.gateway.client.message.codec;

import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public interface DeviceMessageCodec {

    Transport getSupportTransport();

    default List<? extends Message> decode(byte[] messageBytes) {
        String json = new String(messageBytes, StandardCharsets.UTF_8);
        if (json.startsWith("{")) {
            return Collections.singletonList(parseFrom(messageBytes));
        }
        if (json.startsWith("[{")) {
            return parseFroms(messageBytes);
        }
        return null;
    }

    Message parseFrom(byte[] messageBytes);

    default List<? extends Message> parseFroms(byte[] messageBytes) {
        return Collections.singletonList(parseFrom(messageBytes));
    }

    byte[] toByteArray(Message message);

    default byte[] encode(Message message) {
        return toByteArray(message);
    }

    default boolean login(LoginRequest loginRequest) {
        return true;
    }

    default void setExt(Object... obj) {
    }

    //TODO sn -> deviceId
    default String deviceId(String sn) {
        return sn;
    }

}
