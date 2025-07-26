package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformInfoBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformInfoBoConverter {

    TrdPlatformInfoEntity fromTrdPlatformInfo(TrdPlatformInfoBo bo);

    List<TrdPlatformInfoBo> toTrdPlatformInfos(List<TrdPlatformInfoEntity> records);

    TrdPlatformInfoBo toTrdPlatformInfo(TrdPlatformInfoEntity entity);

}
