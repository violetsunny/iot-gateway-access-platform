package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class TripartitePlatformInfoPageQueryBo extends PageQuery {

    private String name;

    //三方平台Code
    private String code;

}
