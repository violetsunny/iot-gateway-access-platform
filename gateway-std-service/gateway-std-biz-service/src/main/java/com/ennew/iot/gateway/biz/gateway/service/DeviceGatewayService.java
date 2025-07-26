package com.ennew.iot.gateway.biz.gateway.service;

import com.ennew.iot.gateway.core.bo.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.HashMap;

public interface DeviceGatewayService {

    boolean save(DeviceGatewayBo bo);

    boolean update(DeviceGatewayBo bo);

    DeviceGatewayResBo getById(String id);

    PageResponse<DeviceGatewayResBo> queryPage(DeviceGatewayPageQueryBo queryPage);

    boolean delete(String id);

    boolean startup(String id);

    boolean pause(String id);

    boolean shutdown(String id);

    MultiResponse<HashMap<String,String>> listNetWorkConfig();

}
