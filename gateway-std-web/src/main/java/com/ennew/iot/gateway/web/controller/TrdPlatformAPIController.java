package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.trd.TrdPlatformApiService;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;
import com.ennew.iot.gateway.dal.enums.AuthWayEnum;
import com.ennew.iot.gateway.dal.enums.BodyParsingMethodEnum;
import com.ennew.iot.gateway.web.util.JwtUtil;
import com.ennew.iot.gateway.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.shade.org.eclipse.util.StringUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@Tag(name = "云平台API管理")
@RequestMapping("/trd/platform/api")
public class TrdPlatformAPIController {

    @Resource
    TrdPlatformApiService trdPlatformApiService;

    @GetMapping("/detail/{id}")
    @Operation(summary = "API详情")
    public SingleResponse<TrdPlatformApiDetailVo> detail(@PathVariable String id) {
        TrdPlatformApiBo trdPlatformApiBo = trdPlatformApiService.getDetailById(id);
        TrdPlatformApiDetailVo trdPlatformApiDetailVo = BeanUtil.copyProperties(trdPlatformApiBo, TrdPlatformApiDetailVo.class);
        if (!CollectionUtils.isEmpty(trdPlatformApiBo.getApiParams())) {
            trdPlatformApiDetailVo.setParamList(BeanUtil.copyToList(trdPlatformApiBo.getApiParams(), TrdPlatformApiParamVo.class));
        }
        return SingleResponse.buildSuccess(trdPlatformApiDetailVo);
    }

    @GetMapping("/list")
    @Operation(summary = "API列表")
    public MultiResponse<TrdPlatformApiVo> list(TrdPlatformApiQueryVo trdPlatformApiQueryVo) {
        TrdPlatformApiQueryBo trdPlatformApiQueryBo = BeanUtil.copyProperties(trdPlatformApiQueryVo, TrdPlatformApiQueryBo.class);
        trdPlatformApiQueryBo.setPCode(trdPlatformApiQueryVo.getPlatformCode());
        MultiResponse<TrdPlatformApiBo> list = trdPlatformApiService.listApi(trdPlatformApiQueryBo);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list.getData(), TrdPlatformApiVo.class));
    }

    @PostMapping("/save")
    @Operation(summary = "新增API")
    public SingleResponse<?> save(@Valid @RequestBody TrdPlatformApiAddVo trdPlatformApiAddVo,@RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        TrdPlatformApiBo trdPlatformApiBo = BeanUtil.copyProperties(trdPlatformApiAddVo, TrdPlatformApiBo.class);
        trdPlatformApiBo.setPCode(trdPlatformApiAddVo.getPlatformCode());
        if (trdPlatformApiService.isExistName(trdPlatformApiAddVo.getPlatformCode(),trdPlatformApiAddVo.getApiName())) {
            return SingleResponse.buildFailure("10001", "名称已存在，请换一个试试");
        }
        trdPlatformApiBo.setHasParam(CollectionUtils.isEmpty(trdPlatformApiAddVo.getParamList()) ? 0 : 1);
        if (!Objects.equals(trdPlatformApiAddVo.getBodyAnalysisType(), BodyParsingMethodEnum.NO.getCode()) && StringUtil.isBlank(trdPlatformApiAddVo.getBodyAnalysisCode())) {
            return SingleResponse.buildFailure("10003", "body解析代码不能为空");
        }
        if (!Objects.equals(trdPlatformApiAddVo.getAuthType(), AuthWayEnum.NO.getCode()) && trdPlatformApiAddVo.getAuthApi() == null) {
            return SingleResponse.buildFailure("10004", "认证接口不能为空");
        }
        trdPlatformApiBo.setApiParams(BeanUtil.copyToList(trdPlatformApiAddVo.getParamList(), TrdPlatformApiParamEntity.class));
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            trdPlatformApiBo.setCreateUser(account);
            trdPlatformApiBo.setUpdateUser(account);
        }
        return SingleResponse.buildSuccess(trdPlatformApiService.saveApi(trdPlatformApiBo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改API")
    public SingleResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody TrdPlatformApiAddVo trdPlatformApiAddVo,@RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        TrdPlatformApiBo trdPlatformApiBo = BeanUtil.copyProperties(trdPlatformApiAddVo, TrdPlatformApiBo.class);
        trdPlatformApiBo.setId(id);
        trdPlatformApiBo.setPCode(trdPlatformApiAddVo.getPlatformCode());
        trdPlatformApiBo.setHasParam(CollectionUtils.isEmpty(trdPlatformApiAddVo.getParamList()) ? 0 : 1);
        trdPlatformApiBo.setApiParams(BeanUtil.copyToList(trdPlatformApiAddVo.getParamList(), TrdPlatformApiParamEntity.class));
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            trdPlatformApiBo.setUpdateUser(account);
        }
        return SingleResponse.buildSuccess(trdPlatformApiService.updateApiById(trdPlatformApiBo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除API")
    public SingleResponse<Boolean> remove(@PathVariable Long id) {
        return SingleResponse.buildSuccess(trdPlatformApiService.removeApiById(id));
    }

}
