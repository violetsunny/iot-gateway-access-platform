package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.DeviceGatewayBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceGatewayBoConverter {
    DeviceGatewayEntity fromDeviceGateway(DeviceGatewayBo bo);

    @Mapping(target = "networkConfiguration", expression = "java(com.ennew.iot.gateway.core.bo.DeviceGatewayResBo.entityToBo(entity.getNetworkConfig()))")
    DeviceGatewayResBo toDeviceGatewayRes(DeviceGatewayEntity entity);

    List<DeviceGatewayResBo> toDeviceGatewayRes(List<DeviceGatewayEntity> records);

}
