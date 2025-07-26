package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ennew.iot.gateway.biz.trd.IDeviceServiceClient;
import com.ennew.iot.gateway.biz.trd.TrdPlatformInfoService;
import com.ennew.iot.gateway.biz.trd.TrdPlatformMeasureRefService;
import com.ennew.iot.gateway.biz.trd.TrdPlatformModelRefService;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.enums.ModelSourceEnum;
import com.ennew.iot.gateway.web.excel.ExcelExportUtil;
import com.ennew.iot.gateway.web.excel.ModelRefExcel;
import com.ennew.iot.gateway.web.excel.ModelRefExportResult;
import com.ennew.iot.gateway.web.excel.ModelRefImportListener;
import com.ennew.iot.gateway.web.util.MinioUtils;
import com.ennew.iot.gateway.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.shade.org.eclipse.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@Tag(name = "云平台模型映射管理")
@RequestMapping("/trd/platform/model/ref")
public class TrdPlatformModelRefController {

    @Resource
    TrdPlatformModelRefService trdPlatformModelRefService;
    @Resource
    TrdPlatformInfoService trdPlatformInfoService;
    @Resource
    TrdPlatformMeasureRefService trdPlatformMeasureRefService;

    @Resource
    IDeviceServiceClient deviceServiceClient;

    @GetMapping("/detail/{id}")
    @Operation(summary = "详情")
    public SingleResponse<TrdPlatformModelRefVo> detail(@PathVariable String id) {
        TrdPlatformModelRefEntity entity = trdPlatformModelRefService.getById(id);
        return SingleResponse.buildSuccess(BeanUtil.copyProperties(entity, TrdPlatformModelRefVo.class));
    }

    @GetMapping("/page")
    @Operation(summary = "三方平台模型映射分页")
    public PageResponse<TrdPlatformModelRefVo> page(TrdPlatformModelRefPageQueryVo trdPlatformModelRefPageQueryVo) {
        TrdPlatformModelRefPageQueryBo trdPlatformModelRefPageQueryBo = BeanUtil.copyProperties(trdPlatformModelRefPageQueryVo, TrdPlatformModelRefPageQueryBo.class);
        PageResponse<TrdPlatformModelRefBo> page = trdPlatformModelRefService.queryPage(trdPlatformModelRefPageQueryBo);
        return PageResponse.of(BeanUtil.copyToList(page.getData(), TrdPlatformModelRefVo.class), page.getTotalCount(), page.getPageSize(), page.getPageNum());
    }

    @GetMapping("/list")
    @Operation(summary = "三方平台模型映射列表")
    public MultiResponse<TrdPlatformModelRefVo> list(TrdPlatformModelRefQueryVo trdPlatformModelRefQueryVo) {
        TrdPlatformModelRefQueryBo trdPlatformModelRefQueryBo = BeanUtil.copyProperties(trdPlatformModelRefQueryVo, TrdPlatformModelRefQueryBo.class);
        MultiResponse<TrdPlatformModelRefBo> list = trdPlatformModelRefService.list(trdPlatformModelRefQueryBo);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list.getData(), TrdPlatformModelRefVo.class));
    }

    @PostMapping("/save")
    @Operation(summary = "新增三方平台模型映射")
    public SingleResponse<?> save(@Valid @RequestBody TrdPlatformModelRefAddVo trdPlatformModelRefAddVo, @RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        String checkResult = trdPlatformModelRefService.entityParamCheck(BeanUtil.copyProperties(trdPlatformModelRefAddVo, TrdPlatformModelRefEntity.class));
        if (StringUtil.isNotBlank(checkResult)) {
            return SingleResponse.buildFailure("10001", checkResult);
        }
        TrdPlatformInfoEntity trdPlatformInfo = trdPlatformInfoService.getByPCode(trdPlatformModelRefAddVo.getPlatformCode());
        if(trdPlatformInfo==null){
            return SingleResponse.buildFailure("10001", trdPlatformModelRefAddVo.getPlatformCode()+" 没有平台信息，请确认信息是否正确");
        }
        trdPlatformModelRefAddVo.setEnnModelSource(StringUtils.hasText(trdPlatformModelRefAddVo.getEnnModelSource())?trdPlatformModelRefAddVo.getEnnModelSource():trdPlatformInfo.getPSource());
        return SingleResponse.buildSuccess(trdPlatformModelRefService.save(trdPlatformModelRefAddVo.createEntity(trdPlatformModelRefAddVo, bladeAuth)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改三方平台模型映射")
    public SingleResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody TrdPlatformModelRefAddVo trdPlatformModelRefAddVo, @RequestHeader(value = "blade-auth", required = false) String bladeAuth) {
        TrdPlatformModelRefEntity trdPlatformModelRefEntity = BeanUtil.copyProperties(trdPlatformModelRefAddVo, TrdPlatformModelRefEntity.class);
        trdPlatformModelRefEntity.setId(id);
        String checkResult = trdPlatformModelRefService.entityParamCheck(trdPlatformModelRefEntity);
        if (StringUtil.isNotBlank(checkResult)) {
            return SingleResponse.buildFailure("10001", checkResult);
        }
        TrdPlatformInfoEntity trdPlatformInfo = trdPlatformInfoService.getByPCode(trdPlatformModelRefAddVo.getPlatformCode());
        if(trdPlatformInfo==null){
            return SingleResponse.buildFailure("10001", trdPlatformModelRefAddVo.getPlatformCode()+" 没有平台信息，请确认信息是否正确");
        }
        trdPlatformModelRefAddVo.setEnnModelSource(StringUtils.hasText(trdPlatformModelRefAddVo.getEnnModelSource())?trdPlatformModelRefAddVo.getEnnModelSource():trdPlatformInfo.getPSource());
        return SingleResponse.buildSuccess(trdPlatformModelRefService.updateById(trdPlatformModelRefAddVo.updateEntity(id, trdPlatformModelRefAddVo, bladeAuth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除三方平台模型映射")
    public SingleResponse<Boolean> remove(@PathVariable Long id) {
        LambdaQueryWrapper<TrdPlatformMeasureRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(TrdPlatformMeasureRefEntity::getModelRefId, id);
        List<TrdPlatformMeasureRefEntity> list = trdPlatformMeasureRefService.list(queryWrapper);
        if(CollectionUtil.isNotEmpty(list)){
            return SingleResponse.buildFailure("10001", "当前模型映射下存在测点映射，请先删除测点映射!");
        }
        return SingleResponse.buildSuccess(trdPlatformModelRefService.removeById(id));
    }

    @Resource
    private MinioUtils minioUtils;

    @Value("${minio.bucket}")
    String bucketName;

    @GetMapping("/template")
    @Operation(summary = "下载模板")
    public void template(HttpServletResponse response) throws Exception {
        String fileName = "模型映射导入模板";
        InputStream fileInputStream = minioUtils.getObject(bucketName, fileName + ExcelExportUtil.XLSX_SUFFIX);
        ExcelExportUtil.excelExport(response, fileInputStream, fileName);
    }

    @PostMapping("/import/{platformCode}")
    @Operation(summary = "批量导入")
    public SingleResponse<ModelRefExportResult> importBatch(@PathVariable String platformCode, MultipartFile file, @RequestParam(value = "tenantId", required = false) String tenantId) {
        String filename = file.getOriginalFilename();
        Assert.hasText(filename, "请上传文件!");
        if ((!StringUtils.endsWithIgnoreCase(filename, ExcelExportUtil.XLS_SUFFIX) && !StringUtils.endsWithIgnoreCase(filename, ExcelExportUtil.XLSX_SUFFIX))) {
            return SingleResponse.buildFailure("20002", "文件格式错误!");
        }
        ModelRefImportListener importListener = new ModelRefImportListener(trdPlatformInfoService,trdPlatformModelRefService, trdPlatformMeasureRefService, deviceServiceClient, platformCode, tenantId);
        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            EasyExcel.read(inputStream, ModelRefExcel.class, importListener)
                    .sheet("模型映射表")
                    .doRead();
            return SingleResponse.buildSuccess(importListener.getResult());
        } catch (IOException e) {
            return SingleResponse.buildFailure("20004", "导入失败");
        }
    }

    @GetMapping("/enum/{type}")
    @Operation(summary = "展示枚举", description = "type=[ModelSourceEnum]")
    public SingleResponse<Map<String, Object>> getEnum(@PathVariable String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("ModelSourceEnum", Arrays.stream(ModelSourceEnum.values()).collect(Collectors.toMap(ModelSourceEnum::getName, ModelSourceEnum::getCode)));
        if ("ALL".equalsIgnoreCase(type)) {
            return SingleResponse.buildSuccess(map);
        }
        return SingleResponse.buildSuccess(Collections.singletonMap(type, map.get(type)));
    }

    @GetMapping("/listByPlatformCode/{platformCode}")
    @Operation(summary = "根据平台code查询模型映射")
    public MultiResponse<TrdPlatformModelRefVo> listByPlatformCode(@PathVariable String platformCode) {
        LambdaQueryWrapper<TrdPlatformModelRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformModelRefEntity::getPlatformCode, platformCode).eq(TrdPlatformModelRefEntity::getIsDelete,0);
        List<TrdPlatformModelRefEntity> modelRefList = trdPlatformModelRefService.list(queryWrapper);
        if (CollectionUtils.isEmpty(modelRefList)) {
            return MultiResponse.buildSuccess();
        }
        List<TrdPlatformModelRefVo> modelRefVoList = BeanUtil.copyToList(modelRefList, TrdPlatformModelRefVo.class);
        modelRefVoList.forEach(vo -> {
            LambdaQueryWrapper<TrdPlatformMeasureRefEntity> measureRefQueryWrapper = new LambdaQueryWrapper<>();
            measureRefQueryWrapper.eq(TrdPlatformMeasureRefEntity::getModelRefId, vo.getId());
            List<TrdPlatformMeasureRefEntity> measureRefList = trdPlatformMeasureRefService.list(measureRefQueryWrapper);
            vo.setTrdPlatformMeasureRefList(BeanUtil.copyToList(measureRefList, TrdPlatformMeasureRefVo.class));
        });
        return MultiResponse.buildSuccess(modelRefVoList);
    }

    @GetMapping("/bind/{ennModelId}")
    @Operation(summary = "查看物模型是否被绑定")
    public SingleResponse<Boolean> isBindModel(@PathVariable String ennModelId) {
        LambdaQueryWrapper<TrdPlatformModelRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformModelRefEntity::getEnnModelId, ennModelId).eq(TrdPlatformModelRefEntity::getIsDelete,0);
        return SingleResponse.buildSuccess(!CollectionUtils.isEmpty(trdPlatformModelRefService.list(queryWrapper)));
    }

}
