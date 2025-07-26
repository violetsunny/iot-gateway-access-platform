package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.gateway.service.NetworkConfigService;
import com.ennew.iot.gateway.core.bo.NetworkConfigQueryBo;
import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;
import com.ennew.iot.gateway.web.converter.NetworkConfigVoConverter;
import com.ennew.iot.gateway.web.vo.NetworkConfigQueryVo;
import com.ennew.iot.gateway.web.vo.NetworkConfigResVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

import java.util.List;

@RestController
@RequestMapping("/network/config")
@Tag(name = "网络组件管理")
@Slf4j
@CatchAndLog
public class NetworkConfigController {

    @Autowired
    private NetworkConfigService networkConfigService;

    @Autowired
    private NetworkConfigVoConverter networkConfigVoConverter;

    @PostMapping("/query")
    @Operation(summary = "查询列表")
    public MultiResponse<NetworkConfigResVo> query(@RequestBody NetworkConfigQueryVo query) {
        NetworkConfigQueryBo queryBo = networkConfigVoConverter.fromNetworkConfigQuery(query);
        List<NetworkConfigResBo> resBos = networkConfigService.query(queryBo);
        return MultiResponse.of(networkConfigVoConverter.toNetworkConfigResList(resBos));
    }

    @GetMapping("/alive")
    @Operation(summary = "查询可使用组件列表")
    public MultiResponse<NetworkConfigResVo> alive(@RequestParam(required = false) String include, @RequestParam(required = false) String type) {
        List<NetworkConfigResBo> resBos = networkConfigService.alive(include, type);
        return MultiResponse.of(networkConfigVoConverter.toNetworkConfigResList(resBos));
    }

}
