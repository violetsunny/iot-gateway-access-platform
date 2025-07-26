package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.kdla.framework.dto.PageQuery;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台分页查询参数")
public class TrdPlatformInfoPageQueryVo extends PageQuery {

    @Schema(description = "平台code")
    private String platformCode;

    @Schema(description = "平台名字")
    private String platformName;

    @Schema(description = "平台类别")
    private Integer platformType;

    @Schema(description = "来源")
    private String platformSource;
}
