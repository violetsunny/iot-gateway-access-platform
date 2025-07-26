package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.kdla.framework.dto.PageQuery;

@Data
@NoArgsConstructor
@Schema(description = "任务分页查询参数")
public class TrdPlatformTaskPageQueryVo extends PageQuery {

    @Schema(description = "任务code")
    private String taskCode;

    @Schema(description = "平台code")
    private String platformCode;

    @Schema(description = "任务名字")
    private String taskName;

    @Schema(description = "产品ID")
    private String productId;

}
