package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.kdla.framework.dto.PageQuery;


@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "云网关关联设备分页查询")
public class CloudGatewayDevicePageQueryVo extends PageQuery {
}
