/**
 * llkang.com Inc.
 * Copyright (c) 2010-2022 All Rights Reserved.
 */
package com.ennew.iot.gateway.integration.device;

import com.ennew.iot.gateway.integration.device.model.DeviceDataBatchReq;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.ennew.iot.gateway.integration.device.model.DeviceStateReq;
import com.ennew.iot.gateway.integration.device.model.PageConditionReq;
import com.ennew.iot.gateway.integration.device.model.PageResponse;
import com.ennew.iot.gateway.integration.device.model.ProjectDataRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;

import java.util.List;

/**
 * 设备管理
 *
 * @author kanglele
 * @version $Id: DeviceClient, v 0.1 2022/8/22 16:03 kanglele Exp $
 */
@FeignClient(url = "${ennew.iot.device.url}", name = "iot-device", fallback = DeviceClientFallback.class)
public interface DeviceClient {

    @GetMapping(value = "/device/get/{deviceId}")
    SingleResponse<DeviceDataRes> getDeviceId(@PathVariable(value = "deviceId") String deviceId);

    @PostMapping(value = "/device/batchUpdateDeviceState")
    SingleResponse batchUpdateDeviceState(@RequestBody DeviceStateReq req);

    @PostMapping(value = "/device/listByConditionPage")
    PageResponse<DeviceDataRes> listByConditionPage(@RequestBody PageConditionReq req);

    @GetMapping(value = "/device/get/bySN/{sn}")
    SingleResponse<DeviceDataRes> getBySN(@PathVariable(value = "sn") String sn);


    @PostMapping(value = "/device/listByDeviceIds")
    MultiResponse<DeviceDataRes> listByDeviceIds(@RequestBody DeviceDataBatchReq req);


    @PostMapping(value = "/project/getSimpleProjectListByIds")
    MultiResponse<ProjectDataRes> getSimpleProjectListByIds(@RequestBody List<String> projectIds);
}
