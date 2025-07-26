/**
 * llkang.com Inc.
 * Copyright (c) 2010-2022 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: EdgeEventDataDTO, v 0.1 2022/11/21 14:42 kanglele Exp $
 */
@NoArgsConstructor
@Data
@Schema(description = "事件上报")
public class HttpEventDataCmd implements Serializable {
    @Schema(description = "版本")
    private String version;
    @Schema(description = "pKey")
    @JsonProperty(value = "pKey")
    @NotBlank(message = "pKey 不能为空")
    private String pKey;
    @Schema(description = "sn")
    private String sn;
    @Schema(description = "事件时间戳")
    @NotNull(message = "ts 不能为空")
    private Long ts;
    @Schema(description = "设备信息")
    @NotNull(message = "devs 不能为空")
    @Valid
    private List<DevsVo> devs;

    @NoArgsConstructor
    @Data
    public static class DevsVo {
        @Schema(description = "sysId")
        private String sysId;
        @Schema(description = "设备ID")
        @NotBlank(message = "devs.dev 不能为空")
        private String dev;
        @Schema(description = "事件标识")
        @NotBlank(message = "devs.identifier 不能为空")
        private String identifier;
        @Schema(description = "时间戳")
        @NotNull(message = "devs.ts 不能为空")
        private Long ts;
        @Schema(description = "事件类型，物模型定义；（info-信息，alarm-告警，fault–故障）")
        private String eventType;
        @Schema(description = "参数信息")
        @NotNull(message = "devs.value 不能为空")
        private Map<String,Object> value;
    }

}