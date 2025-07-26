package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.tripartite.TripartitePlatformInfoService;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoQueryBo;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import com.ennew.iot.gateway.web.vo.TripartitePlatformInfoPageQueryVo;
import com.ennew.iot.gateway.web.vo.TripartitePlatformInfoQueryVo;
import com.ennew.iot.gateway.web.vo.TripartitePlatformInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

/**
 * 三方平台管理
 */
@Slf4j
@RestController
@Validated
@Tag(name = "三方平台管理")
@RequestMapping("/tripartite/platform")
public class TripartitePlatformInfoController {

    @Resource
    TripartitePlatformInfoService tripartitePlatformInfoService;

    @GetMapping("/detail/{id}")
    @Operation(summary = "三方平台详情")
    public SingleResponse<TripartitePlatformInfoVo> detail(@PathVariable String id) {
        TripartitePlatformInfoEntity entity = tripartitePlatformInfoService.getById(id);
        return SingleResponse.buildSuccess(BeanUtil.copyProperties(entity, TripartitePlatformInfoVo.class));
    }

    @GetMapping("/page")
    @Operation(summary = "三方平台分页")
    public PageResponse<TripartitePlatformInfoVo> page(TripartitePlatformInfoPageQueryVo tripartitePlatformInfoPageQueryVo) {
        PageResponse<TripartitePlatformInfoBo> page = tripartitePlatformInfoService.queryPage(BeanUtil.copyProperties(tripartitePlatformInfoPageQueryVo, TripartitePlatformInfoPageQueryBo.class));
        return PageResponse.of(BeanUtil.copyToList(page.getData(), TripartitePlatformInfoVo.class), page.getTotalCount(), page.getPageSize(), page.getPageNum());
    }

    @GetMapping("/list")
    @Operation(summary = "三方平台列表")
    public MultiResponse<TripartitePlatformInfoVo> list(TripartitePlatformInfoQueryVo tripartitePlatformInfoQueryVo) {
        MultiResponse<TripartitePlatformInfoBo> list = tripartitePlatformInfoService.list(BeanUtil.copyProperties(tripartitePlatformInfoQueryVo, TripartitePlatformInfoQueryBo.class));
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list.getData(), TripartitePlatformInfoVo.class));
    }

    @PostMapping("/save")
    @Operation(summary = "新增三方平台")
    public SingleResponse<Boolean> save(@Valid @RequestBody TripartitePlatformInfoVo tripartitePlatformInfoVo) {
        if (tripartitePlatformInfoService.isExistName(tripartitePlatformInfoVo.getName())) {
            return SingleResponse.buildFailure("10001", "名称已存在，请换一个试试");
        }
        if (tripartitePlatformInfoService.isExistCode(tripartitePlatformInfoVo.getCode())) {
            return SingleResponse.buildFailure("10002", "Code已存在，请换一个试试");
        }
        TripartitePlatformInfoEntity entity = BeanUtil.copyProperties(tripartitePlatformInfoVo, TripartitePlatformInfoEntity.class);
        entity.setIsDeleted(0);
        Date current = new Date();
        entity.setCreateTime(current);
        entity.setUpdateTime(current);
        return SingleResponse.buildSuccess(tripartitePlatformInfoService.save(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改三方平台")
    public SingleResponse<Boolean> update(@PathVariable String id, @Valid @RequestBody TripartitePlatformInfoVo tripartitePlatformInfoVo) {
        TripartitePlatformInfoEntity entity = BeanUtil.copyProperties(tripartitePlatformInfoVo, TripartitePlatformInfoEntity.class);
        entity.setId(id);
        Date current = new Date();
        entity.setUpdateTime(current);
        return SingleResponse.buildSuccess(tripartitePlatformInfoService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除三方平台")
    public SingleResponse<Boolean> remove(@PathVariable String id) {
        return SingleResponse.buildSuccess(tripartitePlatformInfoService.removeById(id));
    }

}
