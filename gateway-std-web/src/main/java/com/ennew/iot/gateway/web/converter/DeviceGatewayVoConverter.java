package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.DeviceGatewayBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayPageQueryBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.web.vo.DeviceGatewayCmdVo;
import com.ennew.iot.gateway.web.vo.DeviceGatewayPageQueryVo;
import com.ennew.iot.gateway.web.vo.DeviceGatewayResVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceGatewayVoConverter {

    @Mapping(target = "state", expression = "java(com.ennew.iot.gateway.dal.enums.NetworkConfigState.convert(cmd.getState()))")
    DeviceGatewayBo fromDeviceGateway(DeviceGatewayCmdVo cmd);

    @Mapping(target = "state", expression = "java(com.ennew.iot.gateway.dal.enums.NetworkConfigState.convert(pageQuery.getState()))")
    DeviceGatewayPageQueryBo fromDeviceGatewayPageQuery(DeviceGatewayPageQueryVo pageQuery);

    DeviceGatewayResVo toDeviceGatewayRes(DeviceGatewayResBo bo);

    List<DeviceGatewayResVo> toDeviceGatewayResList(List<DeviceGatewayResBo> data);
}
