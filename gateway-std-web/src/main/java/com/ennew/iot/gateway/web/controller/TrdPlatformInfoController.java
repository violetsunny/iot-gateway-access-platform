package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ennew.iot.gateway.biz.trd.TrdPlatformInfoService;
import com.ennew.iot.gateway.biz.trd.TrdPlatformModelRefService;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.enums.*;
import com.ennew.iot.gateway.web.vo.TrdPlatformInfoAddVo;
import com.ennew.iot.gateway.web.vo.TrdPlatformInfoPageQueryVo;
import com.ennew.iot.gateway.web.vo.TrdPlatformInfoQueryVo;
import com.ennew.iot.gateway.web.vo.TrdPlatformInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@Tag(name = "云平台管理")
@RequestMapping("/trd/platform")
public class TrdPlatformInfoController {

    @Resource
    TrdPlatformInfoService trdPlatformInfoService;

    @Resource
    TrdPlatformModelRefService trdPlatformModelRefService;

    @GetMapping("/detail/{id}")
    @Operation(summary = "平台详情")
    public SingleResponse<TrdPlatformInfoVo> detail(@PathVariable String id) {
        TrdPlatformInfoEntity entity = trdPlatformInfoService.getById(id);
        TrdPlatformInfoVo trdPlatformInfoVo = BeanUtil.copyProperties(entity, TrdPlatformInfoVo.class);
        trdPlatformInfoVo.setCreateTime(DateUtil.format(entity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        trdPlatformInfoVo.setUpdateTime(DateUtil.format(entity.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
        trdPlatformInfoVo.setPSource(StringUtils.isNotBlank(trdPlatformInfoVo.getPSource())?trdPlatformInfoVo.getPSource():ModelSourceEnum.CUSTOM.getCode());
        return SingleResponse.buildSuccess(trdPlatformInfoVo);
    }

    @GetMapping("/page")
    @Operation(summary = "三方平台分页")
    public PageResponse<TrdPlatformInfoVo> page(TrdPlatformInfoPageQueryVo trdPlatformInfoPageQueryVo) {
        TrdPlatformInfoPageQueryBo trdPlatformInfoPageQueryBo = BeanUtil.copyProperties(trdPlatformInfoPageQueryVo, TrdPlatformInfoPageQueryBo.class);
        trdPlatformInfoPageQueryBo.setPCode(trdPlatformInfoPageQueryVo.getPlatformCode());
        trdPlatformInfoPageQueryBo.setPName(trdPlatformInfoPageQueryVo.getPlatformName());
        trdPlatformInfoPageQueryBo.setPType(trdPlatformInfoPageQueryVo.getPlatformType());
        trdPlatformInfoPageQueryBo.setPSource(trdPlatformInfoPageQueryVo.getPlatformSource());
        PageResponse<TrdPlatformInfoBo> page = trdPlatformInfoService.queryPage(trdPlatformInfoPageQueryBo);
        List<TrdPlatformInfoVo> trdPlatformInfoVoList = new ArrayList<>();
        page.getData().forEach(bo->{
            TrdPlatformInfoVo trdPlatformInfoVo = BeanUtil.copyProperties(bo, TrdPlatformInfoVo.class);
            trdPlatformInfoVo.setCreateTime(DateUtil.format(bo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            trdPlatformInfoVo.setUpdateTime(DateUtil.format(bo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            trdPlatformInfoVo.setPSource(StringUtils.isNotBlank(bo.getPSource())?bo.getPSource():ModelSourceEnum.CUSTOM.getCode());
            trdPlatformInfoVoList.add(trdPlatformInfoVo);
        });
        return PageResponse.of(trdPlatformInfoVoList, page.getTotalCount(), page.getPageSize(), page.getPageNum());
    }

    @GetMapping("/list")
    @Operation(summary = "三方平台列表")
    public MultiResponse<TrdPlatformInfoVo> list(TrdPlatformInfoQueryVo trdPlatformInfoQueryVo) {
        TrdPlatformInfoQueryBo trdPlatformInfoQueryBo = BeanUtil.copyProperties(trdPlatformInfoQueryVo, TrdPlatformInfoQueryBo.class);
        trdPlatformInfoQueryBo.setPCode(trdPlatformInfoQueryVo.getPlatformCode());
        trdPlatformInfoQueryBo.setPName(trdPlatformInfoQueryVo.getPlatformName());
        trdPlatformInfoQueryBo.setPType(trdPlatformInfoQueryVo.getPlatformType());
        trdPlatformInfoQueryBo.setPSource(trdPlatformInfoQueryVo.getPlatformSource());
        MultiResponse<TrdPlatformInfoBo> list = trdPlatformInfoService.list(trdPlatformInfoQueryBo);
        List<TrdPlatformInfoVo> trdPlatformInfoVoList = new ArrayList<>();
        list.getData().forEach(bo->{
            TrdPlatformInfoVo trdPlatformInfoVo = BeanUtil.copyProperties(bo, TrdPlatformInfoVo.class);
            trdPlatformInfoVo.setCreateTime(DateUtil.format(bo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            trdPlatformInfoVo.setUpdateTime(DateUtil.format(bo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            trdPlatformInfoVo.setPSource(StringUtils.isNotBlank(bo.getPSource())?bo.getPSource():ModelSourceEnum.CUSTOM.getCode());
            trdPlatformInfoVoList.add(trdPlatformInfoVo);
        });
        return MultiResponse.buildSuccess(trdPlatformInfoVoList);
    }

    @PostMapping("/save")
    @Operation(summary = "新增三方平台")
    public SingleResponse<?> save(@Valid @RequestBody TrdPlatformInfoAddVo trdPlatformInfoAddVo, @RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        if (trdPlatformInfoService.isExistName(trdPlatformInfoAddVo.getPlatformName())) {
            return SingleResponse.buildFailure("10001", "名称已存在，请换一个试试");
        }
        if (trdPlatformInfoService.isExistCode(trdPlatformInfoAddVo.getPlatformCode())) {
            return SingleResponse.buildFailure("10002", "Code已存在，请换一个试试");
        }
        return SingleResponse.buildSuccess(trdPlatformInfoService.save(trdPlatformInfoAddVo.createEntity(trdPlatformInfoAddVo, bladeAuth)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改三方平台")
    public SingleResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody TrdPlatformInfoAddVo trdPlatformInfoAddVo, @RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        return SingleResponse.buildSuccess(trdPlatformInfoService.updateById(trdPlatformInfoAddVo.updateEntity(id, trdPlatformInfoAddVo, bladeAuth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除三方平台")
    public SingleResponse<Boolean> remove(@PathVariable Long id) {
        TrdPlatformInfoEntity trdPlatformInfo = trdPlatformInfoService.getById(id);
        if (trdPlatformInfo == null) {
            return SingleResponse.buildFailure("10002", "对象不存在!");
        }
        LambdaQueryWrapper<TrdPlatformModelRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformModelRefEntity::getPlatformCode, trdPlatformInfo.getPCode());
        List<TrdPlatformModelRefEntity> list = trdPlatformModelRefService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            return SingleResponse.buildFailure("10001", "当前三方平台下存在模型映射关系，请先删除模型映射!");
        }
        return SingleResponse.buildSuccess(trdPlatformInfoService.removeById(id));
    }

    @GetMapping("/enum/{type}")
    @Operation(summary = "展示枚举", description = "type=[ALL、HttpMethod、AuthWay(认证方式)、BodyParsingMethod(body解析方式)、TotalDataGatWay(分页总数获取方式)、ParamPosition(入参位置)、ParamType(入参类型)、PlatformType(平台类型)、ApiType(接口类型)、FunctionType(功能类型)]")
    public SingleResponse<Map<String, Object>> getEnum(@PathVariable String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("HttpMethod", Arrays.stream(HttpMethodEnum.values()).collect(Collectors.toMap(HttpMethodEnum::getName, HttpMethodEnum::getCode)));
        map.put("AuthWay", Arrays.stream(AuthWayEnum.values()).collect(Collectors.toMap(AuthWayEnum::getName, AuthWayEnum::getCode)));
        map.put("BodyParsingMethod", Arrays.stream(BodyParsingMethodEnum.values()).collect(Collectors.toMap(BodyParsingMethodEnum::getName, BodyParsingMethodEnum::getCode)));
        map.put("TotalDataGatWay", Arrays.stream(TotalDataGetWayEnum.values()).collect(Collectors.toMap(TotalDataGetWayEnum::getName, TotalDataGetWayEnum::getCode)));
        map.put("ParamPosition", Arrays.stream(ParamPositionEnum.values()).collect(Collectors.toMap(ParamPositionEnum::getName, ParamPositionEnum::getCode)));
        map.put("ParamType", Arrays.stream(ParamTypeEnum.values()).collect(Collectors.toMap(ParamTypeEnum::getName, ParamTypeEnum::getCode)));
        map.put("PlatformType", Arrays.stream(PlatformTypeEnum.values()).collect(Collectors.toMap(PlatformTypeEnum::getName, PlatformTypeEnum::getCode)));
        map.put("cloudGatewayType", Arrays.stream(PlatformTypeEnum.values())
                .collect(Collectors.groupingBy(PlatformTypeEnum::getTag, Collectors.toMap(PlatformTypeEnum::getName, PlatformTypeEnum::getCode))));
        map.put("ApiType", Arrays.stream(ApiTypeEnum.values()).collect(Collectors.toMap(ApiTypeEnum::getName, ApiTypeEnum::getCode)));
        map.put("FunctionType", Arrays.stream(FunctionTypeEnum.values()).collect(Collectors.toMap(FunctionTypeEnum::getName, FunctionTypeEnum::getCode)));
        if ("ALL".equalsIgnoreCase(type)) {
            return SingleResponse.buildSuccess(map);
        }
        return SingleResponse.buildSuccess(Collections.singletonMap(type, map.get(type)));
    }

}
