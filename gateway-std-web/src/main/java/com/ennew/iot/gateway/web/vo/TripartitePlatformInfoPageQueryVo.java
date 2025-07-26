package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
@Schema(description = "三方平台分页查询vo")
public class TripartitePlatformInfoPageQueryVo extends PageQuery {

    @Schema(description = "三方平台名称")
    private String name;

    @Schema(description = "三方平台Code")
    private String code;

}
