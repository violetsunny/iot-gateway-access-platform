package com.ennew.iot.gateway.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * @Author: lyz
 * Description: http 设备工况上报
 * @date: 上午9:40 2023/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HttpGatewayStatusBo {
    private String ver;
    private String pKey;
    private String sn;
    private String type;
    private Long seq;
    private Long ts;

    private Map<String,Object> data;
}
