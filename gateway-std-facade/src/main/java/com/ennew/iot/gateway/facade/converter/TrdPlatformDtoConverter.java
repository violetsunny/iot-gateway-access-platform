/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.facade.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ennew.iot.gateway.client.dto.*;
import com.ennew.iot.gateway.core.bo.*;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: TrdPlatformDtoConverter, v 0.1 2024/3/19 17:52 kanglele Exp $
 */
@Mapper(componentModel = "spring")
public interface TrdPlatformDtoConverter {

    List<TrdPlatformTaskDto> toTrdPlatformTasks(List<TrdPlatformTaskBo> bos);

    TrdPlatformTaskDto toTrdPlatformTask(TrdPlatformTaskBo bo);

    @Named("intToBoolean")
    default boolean intToBoolean(int status) {
        return status == 1;
    }

    @Mappings({
            @Mapping(source = "configJson", target = "configJson", qualifiedByName = "stringToMap")
    })
    TrdPlatformInfoDto toTrdPlatformInfo(TrdPlatformInfoBo bo);

    List<TrdPlatformInfoDto> toTrdPlatformInfos(List<TrdPlatformInfoBo> bos);

    @Named("stringToMap")
    default Map<String, String> stringToMap(String configJson) {
        if(StringUtils.isBlank(configJson)){
            return null;
        }
        return JSONObject.parseObject(configJson, new TypeReference<Map<String, String>>(){});
    }

    @Mappings({
            @Mapping(target = "method", expression = "java(com.ennew.iot.gateway.dal.enums.HttpMethodEnum.getName(bo.getMethod()))"),
            @Mapping(target = "hasParam", expression = "java(bo.getHasParam() == 1)"),
            @Mapping(target = "hasPages", expression = "java(bo.getHasPages() == 1)")
    })
    TrdPlatformApiDto toTrdPlatformApi(TrdPlatformApiBo bo);

    List<TrdPlatformApiParamDto> toTrdPlatformApiParams(List<TrdPlatformApiParamBo> bos);

    TrdPlatformApiParamDto toTrdPlatformApiParam(TrdPlatformApiParamBo bo);

    @Mapping(source = "refBos", target = "trdPlatformMeasureRefList")
    TrdPlatformModelRefDto toTrdPlatformModelRef(TrdPlatformModelRefBo modelRefBo, List<TrdPlatformMeasureRefBo> refBos);

    List<TrdPlatformMeasureRefDto> toTrdPlatformMeasureRefs(List<TrdPlatformMeasureRefBo> refBos);
}
