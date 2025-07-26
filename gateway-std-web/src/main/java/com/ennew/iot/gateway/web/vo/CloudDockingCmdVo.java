package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:54 2023/7/11
 */
@Schema(description = "CloudDockingCmdVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingCmdVo {

    @Schema(description = "三方云平台名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，三方云平台名称不能为空！", groups = ValidationGroups.Insert.class)
    private String name;

    @Schema(description = "三方云平台编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，三方云平台编码不能为空！", groups = ValidationGroups.Insert.class)
    private String code;

    @Schema(description = "三方云平台URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，三方云平台URL不能为空！", groups = ValidationGroups.Insert.class)
    private String baseUrl;
}
