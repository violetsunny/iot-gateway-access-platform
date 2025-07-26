package com.ennew.iot.gateway.biz.gateway.service.impl;

import com.ennew.iot.gateway.biz.gateway.service.NetworkConfigService;
import com.ennew.iot.gateway.core.bo.NetworkConfigQueryBo;
import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;
import com.ennew.iot.gateway.core.repository.NetworkConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kdla.framework.common.aspect.watch.StopWatchWrapper;

import java.util.List;

@Service
@Slf4j
public class NetworkConfigServiceImpl implements NetworkConfigService {

    @Autowired
    private NetworkConfigRepository networkConfigRepository;

    @Override
    @StopWatchWrapper(logHead = "NetworkConfig", msg = "查询网络组件列表")
    public List<NetworkConfigResBo> query(NetworkConfigQueryBo query) {
        return networkConfigRepository.query(query);
    }

    @Override
    public List<NetworkConfigResBo> alive(String include, String type) {
        return networkConfigRepository.alive(include, type);
    }
}
