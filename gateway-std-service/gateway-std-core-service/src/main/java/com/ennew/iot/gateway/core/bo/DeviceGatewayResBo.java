package com.ennew.iot.gateway.core.bo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.Data;

import java.util.Map;

@Data
public class DeviceGatewayResBo {
    private String id;

    private String name;

    private String type;

    private String protocol;

    private String protocolName;

    private NetworkConfigState state;

    private Map<String, Object> configuration;

    private Map<String, Object> networkConfiguration;

    private String networkId;

    private String description;


    public static Map<String, Object> entityToBo(NetworkConfigEntity entity) {
        return BeanUtil.beanToMap(entity);
    }
}
