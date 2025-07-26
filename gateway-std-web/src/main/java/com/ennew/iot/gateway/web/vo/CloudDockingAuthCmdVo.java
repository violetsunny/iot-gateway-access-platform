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
@Schema(description = "CloudDockingAuthCmdVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingAuthCmdVo {

    @Schema(description = "请求URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，请求URL不能为空！", groups = ValidationGroups.Insert.class)
    private String requestUrl;

    @Schema(description = "请求方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，请求方法不能为空！", groups = ValidationGroups.Insert.class)
    private String requestMethod;

    @Schema(description = "请求参数类型 param,body,path,header", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，请求参数类型不能为空！", groups = ValidationGroups.Insert.class)
    private String requestType;

    @Schema(description = "数据根路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rootPath;
//    @Schema(description = "accessKey", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String accessKey;
//
//    @Schema(description = "accessPrefix", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String accessPrefix;
//
//    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
//    private Long expireTime;

}
