package com.ennew.iot.gateway.core.bo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class DeviceGatewayPageQueryBo extends PageQuery {
    private String id;

    private String name;

    private String type;

    private String protocol;

    private NetworkConfigState state;
}
