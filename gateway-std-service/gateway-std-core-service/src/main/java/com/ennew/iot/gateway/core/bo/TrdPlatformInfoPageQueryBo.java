package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class TrdPlatformInfoPageQueryBo extends PageQuery {

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
