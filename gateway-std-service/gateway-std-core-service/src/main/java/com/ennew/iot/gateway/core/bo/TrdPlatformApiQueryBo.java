package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrdPlatformApiQueryBo implements Serializable {

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 配置类型 ：认证、数据、分页
     */
    private Integer apiType;

    /**
     * 接口名称
     */
    private String apiName;

}
