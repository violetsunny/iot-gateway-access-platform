package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.ProtocolSupportBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportResBo;
import com.ennew.iot.gateway.dal.entity.ProtocolSupportEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProtocolSupportBoConverter {
    ProtocolSupportEntity fromProtocolSupport(ProtocolSupportBo bo);

    ProtocolSupportResBo toProtocolSupportRes(ProtocolSupportEntity entity);

    List<ProtocolSupportResBo> toProtocolSupportRes(List<ProtocolSupportEntity> records);
}
