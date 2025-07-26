/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.bo.SessionPageQueryBo;
import com.ennew.iot.gateway.web.vo.DeviceSessionResVo;
import com.ennew.iot.gateway.web.vo.SessionPageQueryVo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author kanglele
 * @version $Id: SessionVoConverter, v 0.1 2023/2/21 19:56 kanglele Exp $
 */
@Mapper(componentModel = "spring")
public interface SessionVoConverter {

    SessionPageQueryBo fromSessionQuery(SessionPageQueryVo queryVo);

    DeviceSessionResVo toLocalSession(DeviceSessionBo data);

    List<DeviceSessionResVo> toLocalSessions(List<DeviceSessionBo> data);
}
