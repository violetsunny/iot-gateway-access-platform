package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformMeasureRefBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformMeasureRefBoConverter {

    TrdPlatformMeasureRefEntity fromTrdPlatformMeasureRef(TrdPlatformMeasureRefBo bo);

    List<TrdPlatformMeasureRefBo> toTrdPlatformMeasureRefs(List<TrdPlatformMeasureRefEntity> records);

    TrdPlatformMeasureRefBo toTrdPlatformMeasureRef(TrdPlatformMeasureRefEntity entity);

}
