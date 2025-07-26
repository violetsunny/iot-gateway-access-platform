package com.ennew.iot.gateway.biz.protocol.management.script;

import com.ennew.iot.gateway.biz.protocol.management.ProtocolSupportLoaderProvider;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupportProvider;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScriptProtocolSupportLoader implements ProtocolSupportLoaderProvider {

    @Autowired
    private SpringContextUtil serviceContext;

    @Override
    public String getProvider() {
        return "script";
    }

    @Override
    public ProtocolSupport load(ProtocolSupportDefinition definition) {
        ProtocolSupportProvider supportProvider = new ScriptProtocolSupportProvider(definition);
        return supportProvider.create(serviceContext);
    }


}
