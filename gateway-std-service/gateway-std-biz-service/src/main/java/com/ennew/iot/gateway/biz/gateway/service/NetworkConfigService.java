package com.ennew.iot.gateway.biz.gateway.service;

import com.ennew.iot.gateway.core.bo.NetworkConfigQueryBo;
import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;

import java.util.List;

public interface NetworkConfigService {

    List<NetworkConfigResBo> query(NetworkConfigQueryBo query);

    List<NetworkConfigResBo> alive(String include, String type);
}
