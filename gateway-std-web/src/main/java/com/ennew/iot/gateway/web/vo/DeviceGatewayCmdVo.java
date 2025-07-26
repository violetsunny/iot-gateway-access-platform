package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.ennew.iot.gateway.web.validate.EnumValid;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "DeviceGatewayCmdVo对象")
@Data
public class DeviceGatewayCmdVo {

    @Schema(description = "网关id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String id;

    @Schema(description = "网关名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，网关名称不能为空！", groups = ValidationGroups.Insert.class)
    private String name;

//    @Schema(description = "网关类型", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotBlank(message = "数据验证失败，网关类型不能为空！", groups = ValidationGroups.Insert.class)
//    private String type;

    @Schema(description = "协议id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，协议id不能为空！", groups = ValidationGroups.Insert.class)
    private String protocol;

    @Schema(description = "网关状态(enabled:启用、paused:暂停、paused:停止)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，网关状态不能为空！", groups = ValidationGroups.Insert.class)
    @EnumValid(target = NetworkConfigState.class, message = "数据验证失败，网关状态值错误")
    private String state;

    @Schema(description = "网关配置")
    private Map<String,Object> configuration;

    @Schema(description = "网络配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，网络配置不能为空！", groups = ValidationGroups.Insert.class)
    private String networkId;

    @Schema(description = "网关描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}
