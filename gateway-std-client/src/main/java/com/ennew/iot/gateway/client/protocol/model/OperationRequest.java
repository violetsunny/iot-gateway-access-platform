package com.ennew.iot.gateway.client.protocol.model;


import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

import java.util.Map;

@Data
public class OperationRequest extends Message {
    Map<String, Object> param;
    long timeStamp;
    public OperationRequest(){
        setMessageType(MessageType.OPERATION_REQ);
    }
}
