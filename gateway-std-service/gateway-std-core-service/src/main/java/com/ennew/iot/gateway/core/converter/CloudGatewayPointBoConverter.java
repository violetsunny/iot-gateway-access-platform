package com.ennew.iot.gateway.core.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.dal.enums.PlatformTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CloudGatewayPointBoConverter {

    @Mappings({
            @Mapping(source = "id", target = "pointId"),
            @Mapping(source = "realDeviceName", target = "realDeviceName"),
            @Mapping(source = "sort", target = "sort"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "connectorType", target = "connectorType"),
            @Mapping(source = "cloudGatewayCode", target = "cloudGatewayCode"),
            @Mapping(source = "remark", target = "remark"),
            @Mapping(source = "configJson", target = "configJson", qualifiedByName = "parseJsonObject")
    })
    CloudGatewayPointBO fromCloudGatewayPointEntity(CloudGatewayPointEntity cloudGatewayPointEntity);

    List<CloudGatewayPointBO> toCloudGatewayPointBOCollection(List<CloudGatewayPointEntity> cloudGatewayPointEntityList);

    @Named("parseConnectorTypeName")
    default String parseConnectorTypeName(Integer connectorType){
        PlatformTypeEnum typeEnum = PlatformTypeEnum.parse(connectorType);
        return typeEnum == null ? null : typeEnum.getName();
    }

    @Named("parseJsonObject")
    default JSONObject parseJsonObject(String jsonString){
        return JSON.parseObject(jsonString);
    }
}
