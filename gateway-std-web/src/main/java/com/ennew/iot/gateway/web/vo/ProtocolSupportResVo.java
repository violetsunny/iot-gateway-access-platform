package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Schema(description = "ProtocolSupportResVo对象")
@Data
public class ProtocolSupportResVo implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "协议名称")
    private String name;

    @Schema(description = "协议类型")
    private String type;

    @Schema(description = "协议上下行[1:上行,2:下行]")
    private Integer way;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private Byte state;

    private Map<String, Object> configuration;

}
