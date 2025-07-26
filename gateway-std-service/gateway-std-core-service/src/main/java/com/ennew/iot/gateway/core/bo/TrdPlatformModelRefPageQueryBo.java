package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class TrdPlatformModelRefPageQueryBo extends PageQuery {

    private String platformCode;

    /**
     * 平台模型名称
     */
    private String platformModelName;

    /**
     * 平台模型编码
     */
    private String platformModelCode;

    private String key;

}
