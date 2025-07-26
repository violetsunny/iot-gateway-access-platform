/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: GatewayDemoVo, v 0.1 2023/2/3 17:58 kanglele Exp $
 */
@Data
@Schema(description = "示例vo")
public class GatewayDemoVo implements Serializable {
    @Schema(description = "设备id")
    private String deviceId;
    @Schema(description = "下行指令")
    private String function;
    @Schema(description = "下行参数")
    private Map<String, String> params;
}
