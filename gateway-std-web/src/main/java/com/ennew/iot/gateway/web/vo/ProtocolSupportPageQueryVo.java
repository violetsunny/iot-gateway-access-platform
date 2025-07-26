package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
@Schema(description = "协议分页查询vo")
public class ProtocolSupportPageQueryVo extends PageQuery {

    @Schema(description = "协议id")
    private String id;

    @Schema(description = "协议名称")
    private String name;

    @Schema(description = "协议类型")
    private String type;

    @Schema(description = "协议类型")
    private Integer way;

    @Schema(description = "协议状态")
    private Byte state;

    @Schema(description = "是否模板")
    private Byte isTemplate;

}
