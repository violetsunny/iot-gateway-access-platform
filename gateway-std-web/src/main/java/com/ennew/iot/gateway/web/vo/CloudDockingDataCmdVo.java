package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:53 2023/7/14
 */
@Schema(description = "CloudDockingDataCmdVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingDataCmdVo {

    @Schema(description = "请求Code,设置多个url的时候需要通过code进行区分", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataCode;

    @Schema(description = "请求URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，请求URL不能为空！", groups = ValidationGroups.Insert.class)
    private String requestUrl;

    @Schema(description = "请求类型，默认json", requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestType;

    @Schema(description = "请求方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，请求方法不能为空！", groups = ValidationGroups.Insert.class)
    private String requestMethod;

    @Schema(description = "报⽂根路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，报⽂根路径不能为空！", groups = ValidationGroups.Insert.class)
    private String rootPath;

    @Schema(description = "是否拆分 0 不拆分 1 拆分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer split;

    @Schema(description = "限流", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer reqLimit;
}
