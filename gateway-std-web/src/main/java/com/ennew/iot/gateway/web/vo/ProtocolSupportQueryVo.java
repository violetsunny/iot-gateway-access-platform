package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "协议查询vo")
public class ProtocolSupportQueryVo {

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

    @Schema(description = "是否需要过滤出未使用的协议")
    private Boolean filterUnused;

    @Schema(description = "指定包含的已使用的协议")
    private String include;

}
