package com.ennew.iot.gateway.web.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayDeviceService;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceMetricBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDevicePageQueryBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import com.ennew.iot.gateway.web.converter.CloudGatewayDeviceVoConverter;
import com.ennew.iot.gateway.web.vo.CloudGatewayDeviceBindVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayDeviceMetricVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayDevicePageQueryVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayDeviceUnbindVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayDeviceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "云网关设备管理")
@Slf4j
@Validated
@RestController
@RequestMapping("/cloud-gateway")
public class CloudGatewayDeviceController {


    @Resource
    private CloudGatewayDeviceService cloudGatewayDeviceService;

    @Resource
    private CloudGatewayDeviceVoConverter cloudGatewayDeviceVoConverter;

    @Operation(summary = "已绑定设备数量")
    @GetMapping("/{gatewayCode}/device/count")
    public SingleResponse<Long> queryDeviceCount(@PathVariable String gatewayCode){
        return SingleResponse.buildSuccess(cloudGatewayDeviceService.queryDeviceCount(gatewayCode));
    }




    @Operation(summary = "绑定设备")
    @PostMapping("/{gatewayCode}/device/bind")
    public SingleResponse<?> bindDevice(@PathVariable String gatewayCode,
                                              @Valid @RequestBody CloudGatewayDeviceBindVo deviceBindVo,
                                              @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        List<String> exists = cloudGatewayDeviceService.exists(gatewayCode, deviceBindVo.getDeviceIdList());
        if(!CollectionUtils.isEmpty(exists)){
            return SingleResponse.buildFailure("10001", "设备"+exists+"已绑定当前网关");
        }
        List<CloudGatewayDeviceEntity> entityList = deviceBindVo.createEntityList(gatewayCode, bladeAuth);
        boolean b = cloudGatewayDeviceService.bindDevice(gatewayCode, entityList);
        return SingleResponse.buildSuccess(b);
    }


    @Operation(summary = "解绑设备")
    @DeleteMapping("/{gatewayCode}/device/unbind")
    public SingleResponse<Boolean> unbindDevice(@PathVariable String gatewayCode, @Valid @RequestBody CloudGatewayDeviceUnbindVo unbindVo){
        boolean b = cloudGatewayDeviceService.unbindDevice(gatewayCode, unbindVo.getDeviceIdList());
        return SingleResponse.buildSuccess(b);
    }

    @Operation(summary = "分页查询")
    @GetMapping("/{gatewayCode}/device/page")
    public PageResponse<CloudGatewayDeviceVo> queryPage(@PathVariable String gatewayCode, CloudGatewayDevicePageQueryVo queryVo){
        CloudGatewayDevicePageQueryBO queryBO = BeanUtil.copyProperties(queryVo, CloudGatewayDevicePageQueryBO.class);
        PageResponse<CloudGatewayDeviceBO> page = cloudGatewayDeviceService.queryPage(gatewayCode, queryBO);
        List<CloudGatewayDeviceVo> cloudGatewayDeviceVoList = cloudGatewayDeviceVoConverter.fromCloudGatewayDeviceBOList(page.getData());
        return PageResponse.of(cloudGatewayDeviceVoList, page.getTotalCount(), page.getPageSize(), page.getPageNum());
    }


    @Operation(summary = "查询网关所有关联设备")
    @GetMapping("/{gatewayCode}/device/list")
    public MultiResponse<CloudGatewayDeviceVo> queryList(@PathVariable String gatewayCode){
        List<CloudGatewayDeviceBO> cloudGatewayDeviceBOS = cloudGatewayDeviceService.queryList(gatewayCode);
        List<CloudGatewayDeviceVo> cloudGatewayDeviceVoList = cloudGatewayDeviceVoConverter.fromCloudGatewayDeviceBOList(cloudGatewayDeviceBOS);
        return MultiResponse.buildSuccess(cloudGatewayDeviceVoList);
    }



    @Operation(summary = "获取设备属性列表")
    @GetMapping("/product/{productId}/device/{deviceId}/metric")
    public MultiResponse<CloudGatewayDeviceMetricVo> queryMetricList(@PathVariable String productId, @PathVariable String deviceId){
        List<CloudGatewayDeviceMetricBO> cloudGatewayDeviceMetricBOS = cloudGatewayDeviceService.queryDeviceMetricList(productId, deviceId);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(cloudGatewayDeviceMetricBOS, CloudGatewayDeviceMetricVo.class));
    }
}
