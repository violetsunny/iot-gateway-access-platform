package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "Api基本信息")
public class TrdPlatformApiQueryVo implements Serializable {

    @Schema(description = "平台code")
    private String platformCode;

    /**
     * 配置类型 ：认证、数据、分页
     */
    @Schema(description = "配置类型")
    private Integer apiType;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;


}
