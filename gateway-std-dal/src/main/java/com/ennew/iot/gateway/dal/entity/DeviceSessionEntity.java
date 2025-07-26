/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kanglele
 * @version $Id: DeviceSessionEntity, v 0.1 2023/2/23 15:17 kanglele Exp $
 */
@Data
@TableName("device_session")
public class DeviceSessionEntity implements Serializable {

    @TableId(type = IdType.AUTO)
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

    private Boolean isDeleted;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
