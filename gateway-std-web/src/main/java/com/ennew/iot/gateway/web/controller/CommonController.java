/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.server.CommonServer;
import com.ennew.iot.gateway.web.converter.CommonVoConverter;
import com.ennew.iot.gateway.web.vo.KeyValueVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

import javax.annotation.Resource;

/**
 * @author kanglele
 * @version $Id: CommonController, v 0.1 2023/11/15 15:22 kanglele Exp $
 */
@RestController
@RequestMapping("/common")
@Tag(name = "通用")
@Slf4j
@CatchAndLog
public class CommonController {

    @Resource
    private CommonServer commonServer;
    @Resource
    private CommonVoConverter commonVoConverter;

    @GetMapping("/network")
    @Operation(summary = "支持通讯协议")
    public MultiResponse<KeyValueVo<String>> network() {
        return MultiResponse.buildSuccess(commonVoConverter.toCommonVo(commonServer.netWork()));
    }

}
