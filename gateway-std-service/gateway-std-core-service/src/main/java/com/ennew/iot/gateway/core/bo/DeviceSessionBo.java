/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author kanglele
 * @version $Id: DeviceSessionBo, v 0.1 2023/2/23 15:38 kanglele Exp $
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class DeviceSessionBo {
    private Long id;

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
    /**
     * 最后一次心跳时间
     */
    private LocalDateTime lastPingTime;
    /**
     * 连接时间
     */
    private LocalDateTime connectTime;
}
