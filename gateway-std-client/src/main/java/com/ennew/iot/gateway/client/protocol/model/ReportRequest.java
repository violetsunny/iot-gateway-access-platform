package com.ennew.iot.gateway.client.protocol.model;


import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

import java.util.Map;

@Data
public class ReportRequest extends Message {
    Map<String, Object> metric;
    long timeStamp;
    long ingestionTime;
    private String resume = "N"; //Y-续传，N-非续传

    public ReportRequest() {
        setMessageType(MessageType.REPORT_REQ);
    }
}
