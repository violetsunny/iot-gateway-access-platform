/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.session.service;

import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.bo.SessionPageQueryBo;
import top.kdla.framework.dto.PageResponse;

/**
 * @author kanglele
 * @version $Id: DeviceSessionService, v 0.1 2023/2/23 17:14 kanglele Exp $
 */
public interface DeviceSessionService {

    /**
     * 分页查询
     * @param queryBo
     * @return
     */
    PageResponse<DeviceSessionBo> page(SessionPageQueryBo queryBo);

    /**
     * 删除
     * @param sessionId
     * @return
     */
    Boolean remove(String sessionId);

    /**
     * 查询
     * @param sessionId
     * @return
     */
    DeviceSessionBo session(String sessionId);

    /**
     * 存储
     * @param bo
     * @return
     */
    Boolean store(DeviceSessionBo bo);

    /**
     * 删除服务节点下的所有session
     * @param serverId
     * @return
     */
    Boolean removeServerId(String serverId);
}
