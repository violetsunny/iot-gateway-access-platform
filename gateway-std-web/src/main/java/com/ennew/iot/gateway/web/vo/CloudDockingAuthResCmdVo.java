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
@Schema(description = "CloudDockingAuthResCmdVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingAuthResCmdVo {

    /**
     * token key
     * */
    @Schema(description = "token key，请求时的key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "token key，不能为空！", groups = ValidationGroups.Insert.class)
    private String accessKey;

    /**
     * token 映射key
     * */
    @Schema(description = "token来源字段，是指返回字段", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "token来源字段，不能为空！", groups = ValidationGroups.Insert.class)
    private String accessRef;

    /**
     * 获取token的前缀
     * */
    @Schema(description = "获取token的前缀，如果REF是返回字段", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "获取token的前缀，不能为空！", groups = ValidationGroups.Insert.class)
    private String accessPrefix;

    /**
     * 类型， CON-常量，REF-变量
     * */
    @Schema(description = "类型 CON-常量，REF-返回变量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "类型，不能为空！", groups = ValidationGroups.Insert.class)
    private String expireType;

    /**
     * token 方式
     * */
    @Schema(description = "token 方式，默认Header", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paramsType = "Header";

    /**
     * 过期时间
     * */
    private Long expireTime;


    /**
     * 过期时间变量key
     * */
    private String expireKey;
}
