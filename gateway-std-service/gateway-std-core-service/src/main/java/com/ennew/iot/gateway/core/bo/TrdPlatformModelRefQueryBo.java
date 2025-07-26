package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrdPlatformModelRefQueryBo implements Serializable {

    private String platformCode;

    /**
     * 平台模型名称
     */
    private String platformModelName;

    /**
     * 平台模型编码
     */
    private String platformModelCode;

}
