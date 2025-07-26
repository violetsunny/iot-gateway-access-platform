/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.client.protocol.model;

import com.ennew.iot.gateway.client.enums.MessageType;
import lombok.Data;

import java.util.Map;

/**
 * @author kanglele
 * @version $Id: EventRequest, v 0.1 2023/5/11 17:18 kanglele Exp $
 */
@Data
public class EventRequest extends Message {

    private Map<String, Object> value;

    private Long timeStamp;

    private String version;

    private String identifier;

    private String type;

    public EventRequest() {
        setMessageType(MessageType.EVENT_REQ);
    }
}
