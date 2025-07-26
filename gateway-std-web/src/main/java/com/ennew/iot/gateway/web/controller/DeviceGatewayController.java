package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.gateway.service.DeviceGatewayService;
import com.ennew.iot.gateway.core.bo.DeviceGatewayBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayPageQueryBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.web.converter.DeviceGatewayVoConverter;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import com.ennew.iot.gateway.web.vo.DeviceGatewayCmdVo;
import com.ennew.iot.gateway.web.vo.DeviceGatewayPageQueryVo;
import com.ennew.iot.gateway.web.vo.DeviceGatewayResVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

@RestController
@RequestMapping("/gateway")
@Tag(name = "网关管理-V2")
@Slf4j
@CatchAndLog
public class DeviceGatewayController {

    @Autowired
    private DeviceGatewayService deviceGatewayService;

    @Autowired
    private DeviceGatewayVoConverter deviceGatewayVoConverter;

    @PostMapping("/add")
    @Operation(summary = "新增网关V2")
    public SingleResponse<Boolean> add(@RequestBody @Validated(ValidationGroups.Insert.class) DeviceGatewayCmdVo cmd) {
        DeviceGatewayBo bo = deviceGatewayVoConverter.fromDeviceGateway(cmd);
        bo.setType("tcp_server");
        return SingleResponse.buildSuccess(deviceGatewayService.save(bo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改数据")
    public SingleResponse<Boolean> update(@PathVariable String id, @RequestBody @Validated(ValidationGroups.Update.class) DeviceGatewayCmdVo cmd) {
        DeviceGatewayBo bo = deviceGatewayVoConverter.fromDeviceGateway(cmd);
        bo.setId(id);
        bo.setType("tcp_server");
        return SingleResponse.buildSuccess(deviceGatewayService.update(bo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public SingleResponse<DeviceGatewayResVo> getById(@PathVariable String id) {
        return SingleResponse.buildSuccess(deviceGatewayVoConverter.toDeviceGatewayRes(deviceGatewayService.getById(id)));
    }

    @PostMapping("/queryPage")
    @Operation(summary = "分页查询")
    public PageResponse<DeviceGatewayResVo> queryPage(@RequestBody DeviceGatewayPageQueryVo pageQuery) {
        DeviceGatewayPageQueryBo queryPageBo = deviceGatewayVoConverter.fromDeviceGatewayPageQuery(pageQuery);
        PageResponse<DeviceGatewayResBo> resPageBo = deviceGatewayService.queryPage(queryPageBo);
        return PageResponse.of(deviceGatewayVoConverter.toDeviceGatewayResList(resPageBo.getData()), resPageBo.getTotalCount(), resPageBo.getPageSize(), resPageBo.getPageNum());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除")
    public SingleResponse<Boolean> delete(@PathVariable String id) {
        return SingleResponse.buildSuccess(deviceGatewayService.delete(id));
    }

    @PostMapping("/{id}/startup")
    @Operation(summary = "启动网关")
    public SingleResponse<Boolean> startup(@PathVariable String id) {
        return SingleResponse.buildSuccess(deviceGatewayService.startup(id));
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "暂停网关")
    public SingleResponse<Boolean> pause(@PathVariable String id) {
        return SingleResponse.buildSuccess(deviceGatewayService.pause(id));
    }

    @PostMapping("/{id}/shutdown")
    @Operation(summary = "停止网关")
    public SingleResponse<Boolean> shutdown(@PathVariable String id) {
        return SingleResponse.buildSuccess(deviceGatewayService.shutdown(id));
    }
}
