package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformApiParamBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformApiParamBoConverter {

    List<TrdPlatformApiParamBo> toTrdPlatformApiParams(List<TrdPlatformApiParamEntity> entity);

    TrdPlatformApiParamBo toTrdPlatformApiParam(TrdPlatformApiParamEntity entity);

}