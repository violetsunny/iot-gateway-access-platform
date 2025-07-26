/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.facade;

import com.alibaba.fastjson.JSON;
import com.ennew.iot.gateway.client.dto.*;
import com.ennew.iot.gateway.client.service.TrdPlatformApiFeignService;
import com.ennew.iot.gateway.common.enums.OperateEnum;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.repository.*;
import com.ennew.iot.gateway.dal.enums.*;
import com.ennew.iot.gateway.facade.converter.TrdPlatformDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.dto.exception.ErrorCode;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kanglele
 * @version $Id: TrdPlatformApiFeignFacade, v 0.1 2024/3/19 16:48 kanglele Exp $
 */
@RestController
@RequestMapping("/trd/api")
@Tag(name = "三方云平台对外api")
@Slf4j
public class TrdPlatformApiFeignFacade implements TrdPlatformApiFeignService {

    @Autowired
    private TrdPlatformTaskRepository trdPlatformTaskRepository;
    @Autowired
    private TrdPlatformInfoRepository trdPlatformInfoRepository;
    @Autowired
    private TrdPlatformApiRepository trdPlatformApiRepository;
    @Autowired
    private TrdPlatformApiParamRepository trdPlatformApiParamRepository;
    @Autowired
    private TrdPlatformModelRefRepository trdPlatformModelRefRepository;
    @Autowired
    private TrdPlatformMeasureRefRepository trdPlatformMeasureRefRepository;
    @Resource
    private TrdPlatformDtoConverter trdPlatformDtoConverter;

    @Override
    @PostMapping("/taskWorkList")
    @Operation(summary = "任务列表")
    public MultiResponse<TrdPlatformTaskDto> taskWorkList(@RequestBody TrdPlatformReqDto reqDto) {
        List<TrdPlatformTaskBo> bos = trdPlatformTaskRepository.queryByCode(reqDto.getCode());
        return MultiResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformTasks(bos));
    }

    @Override
    @PostMapping("/taskWork")
    @Operation(summary = "任务")
    public SingleResponse<TrdPlatformTaskDto> taskWork(@RequestBody TrdPlatformReqDto reqDto) {
        TrdPlatformTaskBo bo = trdPlatformTaskRepository.searchByCode(reqDto.getCode(),reqDto.getProductId(),reqDto.getTaskCode());
        return SingleResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformTask(bo));
    }

    @Override
    @PostMapping("/trdInfo")
    @Operation(summary = "三方平台信息")
    public SingleResponse<TrdPlatformInfoDto> trdInfo(@RequestBody TrdPlatformReqDto reqDto) {
        TrdPlatformInfoBo bo = trdPlatformInfoRepository.queryByCode(reqDto.getCode());
        return SingleResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformInfo(bo));
    }

    @Override
    @GetMapping("/trdInfo/{ptype}")
    @Operation(summary = "三方平台信息")
    public MultiResponse<TrdPlatformInfoDto> trdInfos(@PathVariable("ptype") Integer ptype) {
        List<TrdPlatformInfoBo> bos = trdPlatformInfoRepository.queryByType(ptype);
        return MultiResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformInfos(bos));
    }

    @Override
    @PostMapping("/apiInfo")
    @Operation(summary = "api接口信息")
    public SingleResponse<TrdPlatformApiDto> apiInfo(@RequestBody TrdPlatformReqDto reqDto) {
        TrdPlatformApiBo bo = trdPlatformApiRepository.getById(reqDto.getApiId());
        return SingleResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformApi(bo));
    }

    @Override
    @PostMapping("/apiParam")
    @Operation(summary = "接口参数")
    public MultiResponse<TrdPlatformApiParamDto> apiParam(@RequestBody TrdPlatformReqDto reqDto) {
        List<TrdPlatformApiParamBo> bos = trdPlatformApiParamRepository.getById(reqDto.getApiId());
        return MultiResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformApiParams(bos));
    }

    @Override
    @PostMapping("/updateTaskStatus")
    @Operation(summary = "更新task状态")
    public SingleResponse<Boolean> updateTaskStatus(@RequestBody TrdPlatformReqDto reqDto) {
        return SingleResponse.buildSuccess(trdPlatformTaskRepository.updateTaskStatus(reqDto.getCode(),reqDto.getProductId(),reqDto.getTaskCode(),reqDto.getStatus()));
    }

    @Override
    @GetMapping("/enum/{type}")
    @Operation(summary = "展示枚举", description = "ALL")
    public SingleResponse<Map<String, Map<String,Object>>> getEnum(@PathVariable("type") String type) {
        Map<String, Map<String,Object>> map = new HashMap<>();
        map.put("HttpMethod", Arrays.stream(HttpMethodEnum.values()).collect(Collectors.toMap(HttpMethodEnum::name, HttpMethodEnum::getCode)));
        map.put("AuthWay", Arrays.stream(AuthWayEnum.values()).collect(Collectors.toMap(AuthWayEnum::name, AuthWayEnum::getCode)));
        map.put("BodyParsingMethod", Arrays.stream(BodyParsingMethodEnum.values()).collect(Collectors.toMap(BodyParsingMethodEnum::name, BodyParsingMethodEnum::getCode)));
        map.put("TotalDataGatWay", Arrays.stream(TotalDataGetWayEnum.values()).collect(Collectors.toMap(TotalDataGetWayEnum::name, TotalDataGetWayEnum::getCode)));
        map.put("ParamPosition", Arrays.stream(ParamPositionEnum.values()).collect(Collectors.toMap(ParamPositionEnum::name, ParamPositionEnum::getCode)));
        map.put("ParamType", Arrays.stream(ParamTypeEnum.values()).collect(Collectors.toMap(ParamTypeEnum::name, ParamTypeEnum::getCode)));
        map.put("PlatformType", Arrays.stream(PlatformTypeEnum.values()).collect(Collectors.toMap(PlatformTypeEnum::name, PlatformTypeEnum::getCode)));
        map.put("ApiType", Arrays.stream(ApiTypeEnum.values()).collect(Collectors.toMap(ApiTypeEnum::name, ApiTypeEnum::getCode)));
        map.put("FunctionType", Arrays.stream(FunctionTypeEnum.values()).collect(Collectors.toMap(FunctionTypeEnum::name, FunctionTypeEnum::getCode)));
        map.put("TaskStatus", Arrays.stream(TaskStatusEnum.values()).collect(Collectors.toMap(TaskStatusEnum::name, TaskStatusEnum::getCode)));
        map.put("Operate", Arrays.stream(OperateEnum.values()).collect(Collectors.toMap(OperateEnum::name, OperateEnum::getCode)));
        if ("ALL".equalsIgnoreCase(type)) {
            return SingleResponse.buildSuccess(map);
        }
        return SingleResponse.buildSuccess(Collections.singletonMap(type, map.get(type)));
    }

    @Override
    @PostMapping("/modelRef")
    @Operation(summary = "三方模型映射")
    public SingleResponse<TrdPlatformModelRefDto> modelRef(@RequestBody TrdPlatformReqDto reqDto) {
        TrdPlatformModelRefBo modelRefBo = trdPlatformModelRefRepository.queryByCode(reqDto.getCode(),reqDto.getProductId());
        if(modelRefBo!=null){
            List<TrdPlatformMeasureRefBo> refBos = trdPlatformMeasureRefRepository.queryById(modelRefBo.getId());
            if(CollectionUtils.isNotEmpty(refBos)){
                return SingleResponse.buildSuccess(trdPlatformDtoConverter.toTrdPlatformModelRef(modelRefBo,refBos));
            }
        }
        return SingleResponse.buildFailure(ErrorCode.FAIL.getCode(),"没有对应三方模型映射信息");
    }
}
