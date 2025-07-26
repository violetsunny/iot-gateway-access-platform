package com.ennew.iot.gateway.web.controller;

import cn.hutool.json.JSONObject;
import com.ennew.iot.gateway.biz.clouddocking.service.CloudDockingService;
import com.ennew.iot.gateway.client.enums.CloudDockingType;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.web.converter.CloudDockingVoConverter;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import com.ennew.iot.gateway.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

import java.util.List;
import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:30 2023/5/25
 */
@RestController
@RequestMapping(value = "/cloud/docking")
@RequiredArgsConstructor
@Tag(name = "云云对接管理")
@Slf4j
@CatchAndLog
public class CloudDockingController {

    private final CloudDockingService cloudDockingService;

    private final CloudDockingVoConverter cloudDockingVoConverter;

    /**
     * 查询列表
     */
    @PostMapping("/queryPage")
    @Operation(summary = "分页查询")
    public PageResponse<CloudDockingResVo> queryPage(@RequestBody CloudDockingPageQueryVo pageQuery) {
        CloudDockingPageQueryBo pageQueryBo = cloudDockingVoConverter.fromDeviceGatewayPageQuery(pageQuery);
        log.info("search condition {} ", pageQueryBo);
        PageResponse<CloudDockingResBO> pageResponse = cloudDockingService.page(pageQueryBo);
        return PageResponse.of(cloudDockingVoConverter.toCloudDockingRes(pageResponse.getData()), pageResponse.getTotalCount(),
                pageResponse.getPageSize(), pageResponse.getPageNum());
    }

    /**
     * 新增云云对接服务
     */
    @PostMapping("/add")
    @Operation(summary = "新增云平台")
    public SingleResponse<Boolean> add(@RequestBody @Validated(ValidationGroups.Insert.class) CloudDockingCmdVo cmd) {
        CloudDockingBO cloudDocking = cloudDockingVoConverter.fromCloudDockingCmd(cmd);
        log.info("add cloud {}", cloudDocking);
        return SingleResponse.buildSuccess(cloudDockingService.saveCloudDocking(cloudDocking));
    }


    @PostMapping("/{id}/startup")
    @Operation(summary = "启用")
    public SingleResponse<Boolean> startup(@PathVariable String id) {
        return SingleResponse.buildSuccess(cloudDockingService.startup(id));
    }


    @PostMapping("/{id}/shutdown")
    @Operation(summary = "禁用")
    public SingleResponse<Boolean> shutdown(@PathVariable String id) {
        return SingleResponse.buildSuccess(cloudDockingService.shutdown(id));
    }

    /**
     * 配置认证信息
     */
    @PostMapping("/{id}/auth/info")
    @Operation(summary = "配置认证信息")
    public SingleResponse<Boolean> configAuthInfo(@PathVariable String id, @RequestBody CloudDockingAuthCmdVo cmd) {
        CloudDockingAuthBO authBO = cloudDockingVoConverter.fromCloudDockingAuthCmd(cmd);
        authBO.setHostId(id);
        log.info("config auth params {}", authBO);
        return SingleResponse.buildSuccess(cloudDockingService.configAuthInfo(authBO));
    }


    /**
     * 配置认证入参
     */
    @PostMapping("/{id}/auth/params")
    @Operation(summary = "配置认证参数")
    public SingleResponse<Boolean> configAuthParams(@PathVariable String id, @RequestBody List<CloudDockingParamsCmdVo> cmd) {
        List<CloudDockingAuthParamsBO> params = cloudDockingVoConverter.fromCloudDockingAuthParamsCmd(cmd);
        log.info("配置参数{}", params);
        return SingleResponse.buildSuccess(cloudDockingService.configAuthParams(id, CloudDockingType.AUTH.getCode(), null, params));
    }

    /**
     * 配置认证出参
     */
    @PostMapping("/{id}/auth/res")
    @Operation(summary = "配置认证响应")
    public SingleResponse<Boolean> configAuthRes(@PathVariable String id, @RequestBody CloudDockingAuthResCmdVo cmd) {
        CloudDockingAuthResBO authBO = cloudDockingVoConverter.fromCloudDockingAuthCmd(cmd);
        authBO.setHostId(id);
        return SingleResponse.buildSuccess(cloudDockingService.configAuthRes(authBO));
    }


    /**
     * 配置数据参数
     */
    @PostMapping("/{id}/data/info")
    @Operation(summary = "配置数据信息")
    public SingleResponse<Boolean> configDataInfo(@PathVariable String id, @RequestBody CloudDockingDataCmdVo cmd) {
        CloudDockingDataBO dataBO = cloudDockingVoConverter.fromCloudDockingDataCmd(cmd);
        dataBO.setHostId(id);
        log.info("config auth params {}", dataBO);
        return SingleResponse.buildSuccess(cloudDockingService.configDataInfo(dataBO));
    }

    /**
     * 根据产品配置参数列表
     */
    @PostMapping("/data/params")
    @Operation(summary = "配置数据参数")
    public SingleResponse<Boolean> configDataParams(@RequestBody CloudDockingDataParamsVo cloudDockingDataParamsVo) {
        List<CloudDockingAuthParamsBO> params = cloudDockingVoConverter.fromCloudDockingAuthParamsCmd(cloudDockingDataParamsVo.getParams());
        log.info("配置参数{}", params);
        return SingleResponse.buildSuccess(cloudDockingService.configAuthParams(cloudDockingDataParamsVo.getId(),
                CloudDockingType.PULL_DATA.getCode(), cloudDockingDataParamsVo.getProdId(), params));
    }

    /**
     * 根据产品配置参数列表
     */
    @PostMapping("/data/metadata")
    @Operation(summary = "配置数据返回")
    public SingleResponse<Boolean> configDataMetadata() {
        //TODO 配置数据返回
        return null;
    }

    @GetMapping(value = "/{id}/auth/detail")
    @Operation(summary = "获取平台认证详情")
    public SingleResponse<CloudDockingAuthDetailVo> getAuthDetail(@PathVariable(value = "id") String id) {
        CloudDockingResBO res = cloudDockingService.getCloudDockingResBO(id);
        CloudDockingAuthBO auth = cloudDockingService.getCloudDockingAuthBO(id);
        CloudDockingAuthResBO authRes = cloudDockingService.getCloudDockingAuthResBO(id);
        List<CloudDockingAuthParamsBO> params = cloudDockingService.cloudDockingAuthParams(id);
        CloudDockingAuthDetailVo detailVo = new CloudDockingAuthDetailVo();
        detailVo.setPlatform(cloudDockingVoConverter.toCloudDockingRes(res));
        detailVo.setRes(cloudDockingVoConverter.fromCloudDockingAuthResBO(authRes));
        detailVo.setBaseInfo(cloudDockingVoConverter.fromCloudDockingAuthBO(auth));
        detailVo.setParams(cloudDockingVoConverter.fromCloudDockingAuthBO(params));
        return SingleResponse.buildSuccess(detailVo);
    }


    @GetMapping(value = "/{id}/data/detail")
    @Operation(summary = "获取平台数据详情")
    public SingleResponse<CloudDockingDataDetailVo> getDataDetail(@PathVariable(value = "id") String id) {
        CloudDockingResBO res = cloudDockingService.getCloudDockingResBO(id);
        List<CloudDockingDataBO> dataBOs = cloudDockingService.getCloudDockingDataBO(id);
        CloudDockingDataDetailVo detailVo = new CloudDockingDataDetailVo();
        detailVo.setPlatform(cloudDockingVoConverter.toCloudDockingRes(res));
        detailVo.setBaseInfo(cloudDockingVoConverter.fromCloudDockingDataBOs(dataBOs));
        return SingleResponse.buildSuccess(detailVo);
    }

    @GetMapping(value = "/{id}/data/params")
    @Operation(summary = "获取数据参数详情")
    public SingleResponse<List<CloudDockingParamsCmdVo>> getDataParamsDetail(
            @PathVariable(value = "id") String id, @RequestParam(value = "prodId", required = false) String prodId) {
        List<CloudDockingAuthParamsBO> params = cloudDockingService.cloudDockingDataParams(id, null, prodId);
        return SingleResponse.buildSuccess(cloudDockingVoConverter.fromCloudDockingAuthBO(params));
    }

    @DeleteMapping(value = "{id}/delete")
    @Operation(summary = "删除")
    public SingleResponse<String> delete(@PathVariable(value = "id") String id) {
        cloudDockingService.deleteById(id);
        return SingleResponse.buildSuccess(id);
    }


    @GetMapping(value = "{id}/mock")
    @Operation(summary = "验证")
    public SingleResponse<JSONObject> mock(@PathVariable(value = "id") String id, @RequestParam(value = "type") String type) {
        return SingleResponse.buildSuccess(cloudDockingService.mockConfig(id, type));
    }

    @PostMapping(value = "/deviceMapping")
    @Operation(summary = "初始化设备映射")
    public SingleResponse<Boolean> deviceMapping(@RequestBody Map<String, String> params) {
        return SingleResponse.buildSuccess(cloudDockingService.deviceMapping(params));
    }
}
