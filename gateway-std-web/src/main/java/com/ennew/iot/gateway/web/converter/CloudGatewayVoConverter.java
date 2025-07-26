package com.ennew.iot.gateway.web.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.enums.PlatformTypeEnum;
import com.ennew.iot.gateway.web.vo.CloudGatewayVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CloudGatewayVoConverter {



    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "PType", target = "cloudGatewayType", qualifiedByName = "parseConnectorTypeName"),
            @Mapping(source = "PCode", target = "cloudGatewayCode"),
            @Mapping(source = "PName", target = "cloudGatewayName"),
            @Mapping(source = "PSource", target = "platformSource"),
            @Mapping(source = "configJson", target = "configJson", qualifiedByName = "parseJsonObject")
    })
    CloudGatewayVo fromTrdPlatformInfoEntity(TrdPlatformInfoEntity entity);




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
