package com.ennew.iot.gateway.client.protocol.model;

import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

@Data
public class LoginRequest extends Message {
    String username;
    String password;
    public LoginRequest(){
        setMessageType(MessageType.LOGIN_REQ);
    }

}
