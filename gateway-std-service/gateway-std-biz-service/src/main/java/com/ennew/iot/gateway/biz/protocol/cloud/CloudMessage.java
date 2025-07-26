/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.protocol.cloud;

import com.ennew.iot.gateway.client.protocol.model.Message;
import lombok.Data;

import java.util.Map;

/**
 * @author kanglele
 * @version $Id: CloudMessage, v 0.1 2023/5/22 18:49 kanglele Exp $
 */
@Data
public class CloudMessage extends Message {

    private String sn;

    private long timestamp;

    private Map<String,Object> properties;

    private String source;

}
