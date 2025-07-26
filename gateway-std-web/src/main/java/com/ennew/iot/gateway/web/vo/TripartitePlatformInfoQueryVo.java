package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "三方平台查询vo")
public class TripartitePlatformInfoQueryVo implements Serializable {

    @Schema(description = "三方平台名称")
    private String name;

    @Schema(description = "三方平台Code")
    private String code;

}
