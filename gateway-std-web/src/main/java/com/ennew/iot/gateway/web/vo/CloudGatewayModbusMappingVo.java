package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Modbus原始点位映射")
@Data
public class CloudGatewayModbusMappingVo {

    @Schema(description = "原始点位ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pointId;

    @Schema(description = "原始点位-原始设备名称")
    private String pointRealDeviceName;

    @Schema(description = "原始点位-点位名称")
    private String pointName;

    @Schema(description = "原始点位-寄存器地址")
    private String pointRegisterAddress;

    @Schema(description = "原始点位-功能码")
    private String pointFunctionCode;

    @Schema(description = "原始点位-数据类型")
    private String pointDataType;

    @Schema(description = "原始点位-字节顺序")
    private String pointByteOrder;


    @Schema(description = "平台-设备名称")
    private String deviceId;

    @Schema(description = "平台-产品ID")
    private String productId;

    @Schema(description = "平台-设备名称")
    private String deviceName;

    @Schema(description = "平台-设备属性名称")
    private String deviceMetricName;

    @Schema(description = "平台-设备属性标识")
    private String deviceMetric;

    @Schema(description = "平台-设备属性单位")
    private String deviceMetricUnit;

    @Schema(description = "平台-设备属性数据类型")
    private String deviceMetricDataType;

    @Schema(description = "平台-设备属性读写类型")
    private String deviceMetricRw;


    @Schema(description = "是否绑定平台设备")
    private boolean bindDevice;
}
