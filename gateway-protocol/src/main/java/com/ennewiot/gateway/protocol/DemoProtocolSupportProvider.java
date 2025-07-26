package com.ennewiot.gateway.protocol;

import com.ennew.iot.gateway.client.protocol.CompositeProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupportProvider;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoProtocolSupportProvider implements ProtocolSupportProvider {
    @Override
    public ProtocolSupport create(SpringContextUtil context) {
        CompositeProtocolSupport support = new CompositeProtocolSupport();
        support.setId("demo.v1.0");
        support.setName("Demo V1.0");
        support.setDescription("Demo Protocol Version 1.0");
        support.addMessageCodecSupport(new DemoDeviceMessageCodec());
        return support;
    }

    @Override
    public void close() {

    }
}
