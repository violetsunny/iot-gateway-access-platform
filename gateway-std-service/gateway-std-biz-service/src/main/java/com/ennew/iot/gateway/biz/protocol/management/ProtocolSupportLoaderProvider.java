package com.ennew.iot.gateway.biz.protocol.management;

import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;

public interface ProtocolSupportLoaderProvider {

    String getProvider();

    ProtocolSupport load(ProtocolSupportDefinition definition);
}
