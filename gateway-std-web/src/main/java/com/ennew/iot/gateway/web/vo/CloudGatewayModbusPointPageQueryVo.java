package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.kdla.framework.dto.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Modbus点位分页查询")
public class CloudGatewayModbusPointPageQueryVo extends PageQuery {


    @Schema(description = "真实设备名称")
    private String realDeviceName;

    @Schema(description = "点位名称")
    private String pointName;


}
