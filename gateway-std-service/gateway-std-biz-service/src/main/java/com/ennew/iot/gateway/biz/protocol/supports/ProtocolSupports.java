package com.ennew.iot.gateway.biz.protocol.supports;

import com.ennew.iot.gateway.client.protocol.ProtocolSupport;

import java.util.List;

public interface ProtocolSupports {

    ProtocolSupport getProtocol(String protocol);

    boolean isSupport(String protocol);

    List<ProtocolSupport> getProtocols();
}
