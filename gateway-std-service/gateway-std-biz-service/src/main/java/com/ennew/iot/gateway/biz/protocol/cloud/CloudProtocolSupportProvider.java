/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.protocol.cloud;

import com.ennew.iot.gateway.client.protocol.CompositeProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupportProvider;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;

import java.io.IOException;

/**
 * @author kanglele
 * @version $Id: CoudProtocolSupport, v 0.1 2023/5/22 17:29 kanglele Exp $
 */
public class CloudProtocolSupportProvider implements ProtocolSupportProvider {

    @Override
    public ProtocolSupport create(SpringContextUtil context) {
        CompositeProtocolSupport support = new CompositeProtocolSupport();
        support.setId("cloudProtocol");
        support.setName("cloudProtocol v1.0");
        support.setDescription("cloudProtocol Version 1.0");
        support.addMessageCodecSupport(new CloudDeviceMessageCodec(context));
        return support;
    }

    @Override
    public void close() throws IOException {

    }

}
