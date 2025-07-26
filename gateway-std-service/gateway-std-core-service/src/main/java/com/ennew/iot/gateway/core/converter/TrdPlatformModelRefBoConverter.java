package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformModelRefBoConverter {

    TrdPlatformModelRefEntity fromTrdPlatformModelRef(TrdPlatformModelRefBo bo);

    List<TrdPlatformModelRefBo> toTrdPlatformModelRefs(List<TrdPlatformModelRefEntity> records);

    TrdPlatformModelRefBo toTrdPlatformModelRef(TrdPlatformModelRefEntity entity);

}
