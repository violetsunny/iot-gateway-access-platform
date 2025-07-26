package com.ennew.iot.gateway.core.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CloudGatewayModbusMappingBO {

    private Long pointId;

    private String pointRealDeviceName;

    private String pointName;

    private String pointRegisterAddress;

    private String pointFunctionCode;

    private String pointDataType;

    private String pointByteOrder;


    private String deviceId;

    private String productId;

    private String deviceName;

    private String deviceMetricName;

    private String deviceMetric;

    private String deviceMetricUnit;

    private String deviceMetricDataType;

    private boolean deviceMetricReadOnly;

    private boolean bindDevice;
}
