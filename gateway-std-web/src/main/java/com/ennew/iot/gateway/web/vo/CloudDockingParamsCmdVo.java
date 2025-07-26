package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @Author: alec
 * Description:
 * @date: 下午3:31 2023/7/12
 */
@Schema(description = "CloudDockingAuthParamsCmdVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingParamsCmdVo {

    @Schema(description = "请求url的Code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataCode;

    @Schema(description = "参数类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，参数类型不能为空！", groups = ValidationGroups.Insert.class)
    private String paramType;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，参数名称不能为空！", groups = ValidationGroups.Insert.class)
    private String paramKey;

    @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，参数值不能为空！", groups = ValidationGroups.Insert.class)
    private String paramValue;

    @Schema(description = "请求分组，相同的组代表是在一次调用的不同参数，多次调用代表不同的组", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reqGroup;
}
