package com.ennew.iot.gateway.client.protocol.model;

import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

@Data
public class LoginResponse  extends Message {
    boolean result;
    long timeStamp;

    public LoginResponse(){
        setMessageType(MessageType.LOGIN_RSP);
    }
}
