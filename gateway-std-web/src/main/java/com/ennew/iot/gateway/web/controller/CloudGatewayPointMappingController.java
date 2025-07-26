package com.ennew.iot.gateway.web.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointMappingService;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointService;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingPageQueryBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointMappingBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusMappingPageQueryVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusMappingVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayPointMappingAddVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayPointMappingBatchAddVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayPointMappingVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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

@Tag(name = "云网关设备测点映射")
@Slf4j
@Validated
@RestController
@RequestMapping("/cloud-gateway")
public class CloudGatewayPointMappingController {

    @Resource
    private CloudGatewayPointService cloudGatewayPointService;
    @Resource
    private CloudGatewayPointMappingService cloudGatewayPointMappingService;


    @Operation(summary = "点位映射保存")
    @PostMapping("/{gatewayCode}/point-mapping/save")
    public SingleResponse<?> savePointMapping(@PathVariable String gatewayCode,
                                                    @Valid @RequestBody CloudGatewayPointMappingAddVo mappingAddVo,
                                                    @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        CloudGatewayPointMappingEntity entity = mappingAddVo.createEntity(gatewayCode, bladeAuth);
        if(cloudGatewayPointMappingService.isBind(gatewayCode, mappingAddVo.getPointId())){
            return SingleResponse.buildFailure("10000", "原始点位已映射，请换一个试试");
        }
        if(!cloudGatewayPointService.exists(mappingAddVo.getPointId())){
            return SingleResponse.buildFailure("10001", "测点不存在");
        }
        if(cloudGatewayPointMappingService.exists(entity)){
            return SingleResponse.buildFailure("10002", "测点映射已存在，请换一个试试");
        }
        return SingleResponse.buildSuccess(cloudGatewayPointMappingService.save(entity));
    }



    @Operation(summary = "点位映射批量保存")
    @PostMapping("/{gatewayCode}/point-mapping/batch-save")
    public SingleResponse<?> batchSavePointMapping(@PathVariable String gatewayCode,
                                                    @Valid @RequestBody CloudGatewayPointMappingBatchAddVo mappingBatchAddVo,
                                                    @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        List<CloudGatewayPointMappingEntity> entityList = mappingBatchAddVo.createEntityList(gatewayCode, bladeAuth);
        return SingleResponse.buildSuccess(cloudGatewayPointMappingService.saveBatch(entityList));
    }



    @Operation(summary = "点位映射删除")
    @DeleteMapping("/{gatewayCode}/point-mapping/{pointId}")
    public SingleResponse<?> deletePointMapping(@PathVariable String gatewayCode, @PathVariable Long pointId,
                                                @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        return SingleResponse.buildSuccess(cloudGatewayPointMappingService.unbind(gatewayCode, pointId));
    }


    @Operation(summary = "点位映射查询-网关获取")
    @GetMapping("/{gatewayCode}/point/mapping")
    public MultiResponse<CloudGatewayPointMappingVo> getPointMapping(@PathVariable String gatewayCode){
        List<CloudGatewayPointMappingBO> pointMapping = cloudGatewayPointMappingService.getPointMapping(gatewayCode);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(pointMapping, CloudGatewayPointMappingVo.class));
    }



    @Operation(summary = "Modbus点位映射查询-分页")
    @GetMapping("/{gatewayCode}/point/modbus/mapping/page")
    public PageResponse<CloudGatewayModbusMappingVo> getPointMappingPage(@PathVariable String gatewayCode,
                                                                         CloudGatewayModbusMappingPageQueryVo queryVo){
        CloudGatewayModbusMappingPageQueryBO queryBO = new CloudGatewayModbusMappingPageQueryBO();
        queryBO.setDeviceId(queryVo.getDeviceId());
        queryBO.setStatus(queryVo.getStatus());
        queryBO.setPageNum(queryVo.getPageNum());
        queryBO.setPageSize(queryVo.getPageSize());
        queryBO.setOrderBy(queryVo.getOrderBy());
        queryBO.setOrderDirection(queryVo.getOrderDirection());
        PageResponse<CloudGatewayModbusMappingBO> response = cloudGatewayPointMappingService.queryModbusMappingPage(gatewayCode, queryBO);
        List<CloudGatewayModbusMappingVo> cloudGatewayModbusMappingVos = BeanUtil.copyToList(response.getData(), CloudGatewayModbusMappingVo.class);
        return PageResponse.of(cloudGatewayModbusMappingVos, response.getTotalCount(), response.getPageSize(), response.getPageNum());

    }
}
