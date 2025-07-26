package com.ennew.iot.gateway.client.protocol.model;


import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

@Data
public class Message {
    private String messageId;
    private String deviceId;
    private String transport;
    private MessageType messageType;
    private String response;
}
