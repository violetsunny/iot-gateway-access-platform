/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.integration.device;

import com.ennew.iot.gateway.integration.device.model.DeviceDataBatchReq;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.ennew.iot.gateway.integration.device.model.DeviceStateReq;
import com.ennew.iot.gateway.integration.device.model.PageConditionReq;
import com.ennew.iot.gateway.integration.device.model.PageResponse;
import com.ennew.iot.gateway.integration.device.model.ProjectDataRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;

import java.util.List;

/**
 * @author kanglele
 * @version $Id: DeviceClientFallback, v 0.1 2023/8/2 17:15 kanglele Exp $
 */
@Component
@Slf4j
public class DeviceClientFallback implements DeviceClient {
    @Override
    public SingleResponse<DeviceDataRes> getDeviceId(String deviceId) {
        return null;
    }

    @Override
    public SingleResponse batchUpdateDeviceState(DeviceStateReq req) {
        return null;
    }

    @Override
    public PageResponse<DeviceDataRes> listByConditionPage(PageConditionReq req) {
        return null;
    }

    @Override
    public SingleResponse<DeviceDataRes> getBySN(String sn) {
        return null;
    }

    @Override
    public MultiResponse<DeviceDataRes> listByDeviceIds(DeviceDataBatchReq req) {
        return null;
    }

    @Override
    public MultiResponse<ProjectDataRes> getSimpleProjectListByIds(List<String> projectIds) {
        return null;
    }

}
