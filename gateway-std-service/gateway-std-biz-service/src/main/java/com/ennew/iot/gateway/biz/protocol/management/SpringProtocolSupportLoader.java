package com.ennew.iot.gateway.biz.protocol.management;

import com.ennew.iot.gateway.biz.protocol.supports.RenameProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.kdla.framework.exception.BizException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpringProtocolSupportLoader implements ProtocolSupportLoader, BeanPostProcessor {

    private final Map<String, ProtocolSupportLoaderProvider> providers = new ConcurrentHashMap<>();

    @Override
    public ProtocolSupport load(ProtocolSupportDefinition definition) {
        ProtocolSupportLoaderProvider provider = providers.get(definition.getProvider());
        if (provider == null) {
            throw new BizException("不支持的provider:" + definition.getProvider());
        }
        return new RenameProtocolSupport(definition.getId(), definition.getName(), definition.getDescription(), provider.load(definition));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ProtocolSupportLoaderProvider) {
            register(((ProtocolSupportLoaderProvider) bean));
        }
        return bean;
    }

    public void register(ProtocolSupportLoaderProvider provider) {
        this.providers.put(provider.getProvider(), provider);
    }
}
