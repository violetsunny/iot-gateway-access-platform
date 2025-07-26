package com.ennew.iot.gateway.integration.device.model.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class IotDevPhysicalModelMeasureQueryReq implements Serializable {

    /**
     * 所查询 量测属性的编码或名称
     */
    String keyword;
    /**
     * 所属设备id
     */
    String deviceId;
    /**
     * 页码
     */
    Integer current;
    /**
     * 页大小
     */
    Integer size;
}
