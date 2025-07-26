package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class CloudGatewayModbusMappingPageQueryBO extends PageQuery {


    private String status;


    private String deviceId;
}
