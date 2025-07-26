package com.ennew.iot.gateway.biz.protocol.supports;

import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import top.kdla.framework.exception.BizException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticProtocolSupports implements ProtocolSupports {

    protected Map<String, ProtocolSupport> supports = new ConcurrentHashMap<>();

    public void register(ProtocolSupport support) {
        ProtocolSupport old = supports.put(support.getId(), support);
        if (null != old) {
            try {
                old.close();
            } catch (IOException e) {
                throw new BizException("发布协议:" + support.getId() + ",旧协议关闭资源失败");
            }
        }
    }

    public void unRegister(ProtocolSupport support) {
        unRegister(support.getId());
    }

    public void unRegister(String id) {
        ProtocolSupport old = supports.remove(id);
        if (null != old) {
            try {
                old.close();
            } catch (IOException e) {
                throw new BizException("取消发布协议:" + id + ",关闭资源失败");
            }
        }
    }

    @Override
    public ProtocolSupport getProtocol(String protocol) {
        ProtocolSupport support = supports.get(protocol);
        if (support == null) {
            throw new BizException("不支持的协议:" + protocol);
        }
        return support;
    }

    @Override
    public boolean isSupport(String protocol) {
        return supports.containsKey(protocol);
    }

    @Override
    public List<ProtocolSupport> getProtocols() {
        return new ArrayList<>(supports.values());
    }
}
