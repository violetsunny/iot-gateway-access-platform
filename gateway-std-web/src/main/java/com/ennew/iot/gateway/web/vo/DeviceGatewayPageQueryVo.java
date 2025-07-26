package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.ennew.iot.gateway.web.validate.EnumValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
@Schema(description = "网关分页查询vo")
public class DeviceGatewayPageQueryVo extends PageQuery {

    @Schema(description = "网关id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String id;

    @Schema(description = "网关名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "网关类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "协议id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String protocol;

    @Schema(description = "网关状态(enabled:启用、paused:暂停、paused:停止)")
    @EnumValid(target = NetworkConfigState.class, message = "数据验证失败，网关状态值错误")
    private String state;
}
