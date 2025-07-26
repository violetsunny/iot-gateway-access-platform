package com.ennew.iot.gateway.web.controller;


import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dtuMag")
@Tag(name = "DTU连接管理")
@Slf4j
@CatchAndLog
public class DtuDeviceConnectController {

    @PostMapping("/queryAll")
    @Operation(summary = "获取全部连接配置信息")
    public SingleResponse<Map<String, MainDataDTO>> queryAll() {

        return SingleResponse.of(IotGlobalContextUtil.MainData.getAllMainData());
    }

    @PostMapping("/queryAllGatewaySn")
    @Operation(summary = "获取连接列表")
    public SingleResponse<Set<String>> queryAllGatewaySn() {

        Set<Map.Entry<String, MainDataDTO>> entries = IotGlobalContextUtil.MainData.getAllMainData().entrySet();
        Set<String> GatewaySns = entries.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        return SingleResponse.of(GatewaySns);
    }

    @PostMapping("/queryDevicesByGatewaySn")
    @Operation(summary = "获取指定网关设备点表信息")
    public SingleResponse<MainDataDTO> queryDevicesByGatewaySn(String gatewaySn) {

        MainDataDTO mainData = IotGlobalContextUtil.MainData.getMainData(gatewaySn);
        return SingleResponse.of(mainData);
    }
}
