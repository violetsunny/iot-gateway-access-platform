package com.ennew.iot.gateway.web.converter;


import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceBO;
import com.ennew.iot.gateway.web.vo.CloudGatewayDeviceVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CloudGatewayDeviceVoConverter {


    @Mappings({
            @Mapping(source = "gatewayCode", target = "gatewayCode"),
            @Mapping(source = "deviceId", target = "deviceId"),
            @Mapping(source = "deviceName", target = "deviceName"),
            @Mapping(source = "entityTypeId", target = "entityTypeId"),
            @Mapping(source = "entityTypeName", target = "entityTypeName"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "productName", target = "productName"),
            @Mapping(source = "projectId", target = "projectId"),
            @Mapping(source = "projectName", target = "projectName")
    })
    CloudGatewayDeviceVo fromCloudGatewayDeviceBO(CloudGatewayDeviceBO cloudGatewayDeviceBO);


    List<CloudGatewayDeviceVo> fromCloudGatewayDeviceBOList(List<CloudGatewayDeviceBO> CloudGatewayDeviceBOList);
}
