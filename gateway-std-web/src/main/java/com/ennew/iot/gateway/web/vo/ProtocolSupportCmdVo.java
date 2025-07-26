package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Map;

@Schema(description = "ProtocolSupportCmdVo对象")
@Data
public class ProtocolSupportCmdVo {

    @Schema(description = "协议id")
    private String id;

    @Schema(description = "协议名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，协议名称不能为空！", groups = ValidationGroups.Insert.class)
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String name;

    @Schema(description = "协议描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String description;

    @Schema(description = "协议类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，协议类型不能为空！", groups = ValidationGroups.Insert.class)
    private String type;

    @Schema(description = "协议上下行[1:上行,2:下行]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer way;

    @Schema(description = "协议状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，协议状态不能为空！", groups = ValidationGroups.Insert.class)
    private Byte state;

    @Schema(description = "协议配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，协议配置不能为空！", groups = ValidationGroups.Insert.class)
    private Map<String, Object> configuration;

}
