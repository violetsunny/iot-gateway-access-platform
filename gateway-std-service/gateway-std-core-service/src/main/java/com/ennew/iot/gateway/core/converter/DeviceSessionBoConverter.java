/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.dal.entity.DeviceSessionEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author kanglele
 * @version $Id: DeviceSessionBoConverter, v 0.1 2023/2/23 15:45 kanglele Exp $
 */
@Mapper(componentModel = "spring")
public interface DeviceSessionBoConverter {

    DeviceSessionEntity fromDeviceSession(DeviceSessionBo bo);

    List<DeviceSessionBo> toDeviceSessions(List<DeviceSessionEntity> records);

    DeviceSessionBo toDeviceSession(DeviceSessionEntity entity);

}
