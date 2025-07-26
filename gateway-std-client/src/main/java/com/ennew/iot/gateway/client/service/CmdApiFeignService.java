/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.client.service;

import com.ennew.iot.gateway.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;

import java.util.Map;

/**
 * @author kanglele
 * @version $Id: TrdPlatformApiFeignService, v 0.1 2024/3/19 16:30 kanglele Exp $
 */
@FeignClient(name="gateway-std",contextId = "CmdApiFeignService")
public interface CmdApiFeignService {

    @RequestMapping(value = "/cmd/sent",method = RequestMethod.POST)
     SingleResponse sentCmdService(@RequestParam String msgId, String deviceId, String status);

}
