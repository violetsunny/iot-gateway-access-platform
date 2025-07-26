/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

/**
 * @author kanglele
 * @version $Id: SessionPageQueryBo, v 0.1 2023/2/21 19:39 kanglele Exp $
 */
@Data
public class SessionPageQueryBo extends PageQuery {

    /**
     * 服务节点
     */
    private String serverId;
    /**
     * 传输协议
     */
    private String transport;
    /**
     * 会话id
     */
    private String sessionId;
    /**
     * 设备id
     */
    private String deviceId;

}
