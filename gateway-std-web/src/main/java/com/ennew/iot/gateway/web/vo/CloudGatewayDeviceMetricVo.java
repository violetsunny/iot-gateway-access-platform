package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备属性VO")
public class CloudGatewayDeviceMetricVo {


    @Schema(description = "设备属性编码")
    private String deviceMetric;


    @Schema(description = "设备属性名称")
    private String deviceMetricName;


    @Schema(description = "设备ID")
    private String deviceId;

}
