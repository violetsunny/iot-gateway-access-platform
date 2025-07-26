package com.ennew.iot.gateway.client.protocol.model;

import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

@Data
public class ReportResponse  extends Message {
    boolean result;
    long timeStamp;

    public ReportResponse(){
        this.setMessageType(MessageType.REPORT_RSP);
    }
}
