package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.TrdPlatformTaskBo;
import com.ennew.iot.gateway.core.message.TrdPlatformTaskMessage;
import com.ennew.iot.gateway.dal.entity.TrdPlatformTaskEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrdPlatformTaskBoConverter {

    TrdPlatformTaskEntity fromTrdPlatformTask(TrdPlatformTaskBo bo);

    List<TrdPlatformTaskBo> toTrdPlatformTasks(List<TrdPlatformTaskEntity> records);

    TrdPlatformTaskBo toTrdPlatformTask(TrdPlatformTaskEntity entity);

    TrdPlatformTaskMessage fromTrdPlatformTaskEntity(TrdPlatformTaskEntity entity);

}
