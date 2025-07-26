package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripartitePlatformInfoBoConverter {

    TripartitePlatformInfoEntity fromTripartitePlatformInfo(TripartitePlatformInfoBo bo);

    List<TripartitePlatformInfoBo> toTripartitePlatformInfos(List<TripartitePlatformInfoEntity> records);

    TripartitePlatformInfoBo toTripartitePlatformInfo(TripartitePlatformInfoEntity entity);

}
