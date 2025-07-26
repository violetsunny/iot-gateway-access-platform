package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台模型映射查询参数")
public class TrdPlatformModelRefQueryVo implements Serializable {

    @Schema(description = "平台编码")
    private String platformCode;

    @Schema(description = "平台模型名称")
    private String platformModelName;

    @Schema(description = "平台模型编码")
    private String platformModelCode;

}
