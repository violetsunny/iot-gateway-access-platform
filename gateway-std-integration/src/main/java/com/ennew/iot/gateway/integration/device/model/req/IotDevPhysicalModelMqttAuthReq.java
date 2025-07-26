package com.ennew.iot.gateway.integration.device.model.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ruanhong
 */
@Data
@Builder
public class IotDevPhysicalModelMqttAuthReq implements Serializable {

    /**
     * 网关标识 网关编码（pkey_sn）
     */
    String clientId;
    /**
     * 站id 站标识（可选）
     */
    String username;
    /**
     * 第三方编码 设备第三方标识
     */
    String password;
}
