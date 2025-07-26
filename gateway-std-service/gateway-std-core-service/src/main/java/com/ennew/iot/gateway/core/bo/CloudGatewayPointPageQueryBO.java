package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class CloudGatewayPointPageQueryBO extends PageQuery {

    private String pointName;


    private String realDeviceName;
}
