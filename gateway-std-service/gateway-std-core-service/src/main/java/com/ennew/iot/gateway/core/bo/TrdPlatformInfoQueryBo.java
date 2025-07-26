package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrdPlatformInfoQueryBo implements Serializable {

    /**
     * 平台类别
     */
    private Integer pType;

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 平台名字
     */
    private String pName;

    private String pSource;
}
