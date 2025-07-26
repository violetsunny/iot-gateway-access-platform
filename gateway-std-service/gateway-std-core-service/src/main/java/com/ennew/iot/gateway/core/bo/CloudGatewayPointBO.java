package com.ennew.iot.gateway.core.bo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CloudGatewayPointBO {

    /**
     * 原始设备名称
     */
    private String realDeviceName;


    /**
     * 测点ID
     */
    private Long pointId;


    /**
     * 测点顺序
     */
    private Integer sort;

    /**
     * 测点名称
     */
    private String name;


    /**
     * 连接器类型
     */
    private Integer connectorType;


    /**
     * 云网关编码
     */
    private String cloudGatewayCode;

    /**
     * 备注
     */
    private String remark;


    /**
     * 配置信息
     */
    private JSONObject configJson;

}
