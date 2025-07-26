package com.ennew.iot.gateway.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备信息")
public class CloudGatewayDeviceVo {

    @Schema(description = "网关编码")
    private String gatewayCode;


    @Schema(description = "设备ID")
    private String deviceId;


    @Schema(description = "设备名称")
    private String deviceName;


    @Schema(description = "物模型ID")
    private String entityTypeId;

    @Schema(description = "物模型名称")
    private String entityTypeName;


    @Schema(description = "产品ID")
    private String productId;


    @Schema(description = "产品名称")
    private String productName;


    @Schema(description = "项目ID")
    private String projectId;


    @Schema(description = "项目名称")
    private String projectName;

}
