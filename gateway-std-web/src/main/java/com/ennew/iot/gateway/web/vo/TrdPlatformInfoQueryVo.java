package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台查询参数")
public class TrdPlatformInfoQueryVo implements Serializable {

    @Schema(description = "平台code")
    private String platformCode;

    @Schema(description = "平台名字")
    private String platformName;

    @Schema(description = "平台类别")
    private Integer platformType;

    @Schema(description = "来源")
    private String platformSource;
}
