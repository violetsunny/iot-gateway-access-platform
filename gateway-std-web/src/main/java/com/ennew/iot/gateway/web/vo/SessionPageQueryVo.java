/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

/**
 * @author kanglele
 * @version $Id: SessionPageQueryBo, v 0.1 2023/2/21 19:39 kanglele Exp $
 */
@Data
@Schema(description = "会话分页查询")
public class SessionPageQueryVo extends PageQuery {
    /**
     * 服务节点
     */
    @Schema(description = "服务节点")
    private String serverId;
    /**
     * 传输协议
     */
    @Schema(description = "组件类型")
    private String transport;
    /**
     * 会话id
     */
    @Schema(description = "会话id")
    private String sessionId;
    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;
}
