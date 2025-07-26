/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.session.service.DeviceSessionService;
import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.bo.SessionPageQueryBo;
import com.ennew.iot.gateway.web.converter.SessionVoConverter;
import com.ennew.iot.gateway.web.vo.DeviceSessionResVo;
import com.ennew.iot.gateway.web.vo.SessionPageQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;

/**
 * @author kanglele
 * @version $Id: SessionController, v 0.1 2023/2/21 16:23 kanglele Exp $
 */
@RestController
@RequestMapping("/session")
@Tag(name = "会话管理")
@Slf4j
public class SessionController {
    @Resource
    private DeviceSessionService deviceSessionService;
    @Resource
    private SessionVoConverter sessionVoConverter;

    @PostMapping("/queryPage")
    @Operation(summary = "分页查询")
    public PageResponse<DeviceSessionResVo> page(@RequestBody SessionPageQueryVo queryVo){
        SessionPageQueryBo queryBo = sessionVoConverter.fromSessionQuery(queryVo);
        PageResponse<DeviceSessionBo> pageResponse = deviceSessionService.page(queryBo);
        return PageResponse.of(sessionVoConverter.toLocalSessions(pageResponse.getData()),pageResponse.getTotalCount(),pageResponse.getPageSize(),pageResponse.getPageNum());
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "根据id获取会话")
    public SingleResponse<DeviceSessionResVo> getSessionById(@PathVariable String sessionId) {
        return SingleResponse.buildSuccess(sessionVoConverter.toLocalSession(deviceSessionService.session(sessionId)));
    }

    @PostMapping("/unregister/{sessionId}")
    @Operation(summary = "移除会话-下线")
    public SingleResponse<Boolean> unregister(@PathVariable String sessionId) {
        //先removeSession中会话，会修改缓存设备状态，然后再调用设备下线
        return SingleResponse.buildSuccess(deviceSessionService.remove(sessionId));
    }

}
