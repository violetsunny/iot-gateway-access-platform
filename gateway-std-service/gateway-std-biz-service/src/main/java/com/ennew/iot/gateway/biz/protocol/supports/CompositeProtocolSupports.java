package com.ennew.iot.gateway.biz.protocol.supports;

import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import top.kdla.framework.exception.BizException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CompositeProtocolSupports implements ProtocolSupports {

    private final List<ProtocolSupports> supports = new CopyOnWriteArrayList<>();

    public void register(ProtocolSupports supports) {
        this.supports.add(supports);
    }

    @Override
    public ProtocolSupport getProtocol(String protocol) {
        for (ProtocolSupports support : supports) {
            if (support.isSupport(protocol)) {
                return support.getProtocol(protocol);
            }
        }
        throw new BizException("不支持的协议:" + protocol);
    }

    @Override
    public boolean isSupport(String protocol) {
        for (ProtocolSupports support : supports) {
            if (support.isSupport(protocol)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ProtocolSupport> getProtocols() {
        List<ProtocolSupport> protocols = new ArrayList<>();
        supports.forEach(support -> {
             protocols.addAll(support.getProtocols());
        });
        return protocols;
    }
}
