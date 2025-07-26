package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NetworkConfigBoConverter {
    NetworkConfigResBo toNetworkConfigRes(NetworkConfigEntity entity);

    List<NetworkConfigResBo> toNetworkConfigRes(List<NetworkConfigEntity> records);
}
