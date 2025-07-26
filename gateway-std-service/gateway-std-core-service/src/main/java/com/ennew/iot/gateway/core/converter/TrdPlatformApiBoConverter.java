package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformApiBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformApiBoConverter {

    TrdPlatformApiEntity fromTrdPlatformApi(TrdPlatformApiBo bo);

    List<TrdPlatformApiBo> toTrdPlatformApis(List<TrdPlatformApiEntity> records);

    TrdPlatformApiBo toTrdPlatformApi(TrdPlatformApiEntity entity);

}
