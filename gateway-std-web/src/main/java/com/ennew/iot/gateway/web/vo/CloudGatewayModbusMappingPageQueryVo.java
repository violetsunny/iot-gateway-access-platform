package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
@Schema(description = "测点映射分页查询vo")
public class CloudGatewayModbusMappingPageQueryVo extends PageQuery {

    @Schema(description = "绑定状态，all=所有，unbind=未绑定，bind=已绑定")
    private String status;

    @Schema(description = "设备ID")
    private String deviceId;
}
