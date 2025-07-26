package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class ProtocolSupportPageQueryBo extends PageQuery {

    private String id;

    private String name;

    private String type;

    private Integer way;

    private Byte state;

    private Byte isTemplate;

    private Integer isDeleted;

}
