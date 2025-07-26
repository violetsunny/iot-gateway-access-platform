/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Data
@Schema(description = "会话管理vo")
public class DeviceSessionResVo {
    @Schema(description = "id")
    private Long id;

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
    /**
     * 最后一次心跳时间
     */
    @Schema(description = "最后一次心跳时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastPingTime;
    /**
     * 连接时间
     */
    @Schema(description = "上线时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime connectTime;
}
