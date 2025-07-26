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
@FeignClient(name="gateway-std",contextId = "TrdPlatformApiFeignService")
public interface TrdPlatformApiFeignService {
    @RequestMapping(value = "/taskWorkList",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    MultiResponse<TrdPlatformTaskDto> taskWorkList(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/taskWork",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<TrdPlatformTaskDto> taskWork(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/trdInfo",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<TrdPlatformInfoDto> trdInfo(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/trdInfo/{ptype}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    MultiResponse<TrdPlatformInfoDto> trdInfos(@PathVariable("ptype") Integer ptype);

    @RequestMapping(value = "/apiInfo",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<TrdPlatformApiDto> apiInfo(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/apiParam",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    MultiResponse<TrdPlatformApiParamDto> apiParam(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/updateTaskStatus",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<Boolean> updateTaskStatus(@RequestBody TrdPlatformReqDto reqDto);

    @RequestMapping(value = "/enum/{type}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<Map<String, Map<String,Object>>> getEnum(@PathVariable("type") String type);

    @RequestMapping(value = "/modelRef",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<TrdPlatformModelRefDto> modelRef(@RequestBody TrdPlatformReqDto reqDto);
}
