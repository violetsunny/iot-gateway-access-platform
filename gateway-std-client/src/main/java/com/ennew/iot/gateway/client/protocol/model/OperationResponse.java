package com.ennew.iot.gateway.client.protocol.model;

import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

@Data
public class OperationResponse extends Message {
    boolean result;
    long timeStamp;

    public OperationResponse(){
        this.setMessageType(MessageType.OPERATION_RSP);
    }
}
