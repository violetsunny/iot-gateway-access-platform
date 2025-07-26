package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "网络组件查询vo")
public class NetworkConfigQueryVo {

    @Schema(description = "组件id")
    private String id;

    @Schema(description = "组件名称")
    private String name;

    @Schema(description = "组件类型")
    private String type;

}
