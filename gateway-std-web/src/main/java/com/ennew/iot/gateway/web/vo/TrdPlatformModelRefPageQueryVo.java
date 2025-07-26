package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.kdla.framework.dto.PageQuery;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台模型映射分页查询参数")
public class TrdPlatformModelRefPageQueryVo extends PageQuery {

    @Schema(description = "平台编码")
    private String platformCode;

    @Schema(description = "平台模型名称")
    private String platformModelName;

    @Schema(description = "平台模型编码")
    private String platformModelCode;

    @Schema(description = "名称或Code")
    private String key;

}
