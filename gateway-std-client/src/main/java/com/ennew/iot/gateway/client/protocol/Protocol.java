package com.ennew.iot.gateway.client.protocol;


import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;

/**
 * 自定义协议必须实现这个接口才能对接
 */
public interface Protocol {

    String getId();

    String getName();

    String getDescription();

    Message parseFrom(byte[] messageBytes);

    byte[] toByteArray(Message message);

    boolean login(LoginRequest loginRequest);
}
