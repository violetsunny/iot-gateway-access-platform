package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "任务查询参数")
public class TrdPlatformTaskQueryVo implements Serializable {

    @Schema(description = "任务code")
    private String taskCode;

    @Schema(description = "平台code")
    private String platformCode;

    @Schema(description = "任务名字")
    private String taskName;

    @Schema(description = "产品Id")
    private String productId;

}
