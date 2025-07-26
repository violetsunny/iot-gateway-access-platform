package com.ennew.iot.gateway.biz.gateway.supports;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.gateway.DeviceGateway;
import com.ennew.iot.gateway.biz.gateway.DeviceGatewayProvider;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.core.repository.DeviceGatewayRepository;
import com.ennew.iot.gateway.core.repository.NetworkConfigRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.kdla.framework.exception.BizException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultDeviceGatewayManager implements DeviceGatewayManager, BeanPostProcessor {

    @Autowired
    private DeviceGatewayRepository deviceGatewayRepository;

    /**
     * 启动状态的设备网关
     */
    private final Map<String, DeviceGateway> store = new ConcurrentHashMap<>();

    private final Map<String, DeviceGatewayProvider> providers = new ConcurrentHashMap<>();

    @Override
    public DeviceGateway getGateway(String id) {
        DeviceGateway deviceGateway = store.get(id);
        if (deviceGateway == null) {
           deviceGateway = doGetGateway(id);
        }
        return deviceGateway;
    }

    @Override
    public void removeGatewayCache(String id) {
        store.remove(id);
    }

    private DeviceGateway doGetGateway(String id) {
        if (store.containsKey(id)) {
            return store.get(id);
        }
        DeviceGatewayResBo bo = deviceGatewayRepository.queryById(id);
        DeviceGatewayProperties properties = new DeviceGatewayProperties();
        BeanUtil.copyProperties(bo, properties);
        properties.setNetworkConfigProperties(BeanUtil.toBean(bo.getNetworkConfiguration(), NetworkConfigProperties.class));
        DeviceGatewayProvider provider = providers.get(properties.getType());
        if (provider == null) {
            throw new BizException("不支持的网络服务[" + properties.getType() + "]");
        }
        DeviceGateway deviceGateway = provider.createDeviceGateway(properties);
        if (store.containsKey(id)) {
            deviceGateway.shutdown();
            return store.get(id);
        }
        store.put(id, deviceGateway);
        return deviceGateway;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DeviceGatewayProvider) {
            DeviceGatewayProvider provider = ((DeviceGatewayProvider) bean);
            providers.put(provider.getId(), provider);
        }
        return bean;
    }
}
