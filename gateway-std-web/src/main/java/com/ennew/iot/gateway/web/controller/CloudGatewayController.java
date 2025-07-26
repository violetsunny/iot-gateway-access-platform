package com.ennew.iot.gateway.web.controller;

import com.alibaba.fastjson.JSON;
import com.ennew.iot.gateway.biz.trd.TrdPlatformInfoService;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.web.converter.CloudGatewayVoConverter;
import com.ennew.iot.gateway.web.vo.CloudGatewayAddVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusConfigVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayUpdateVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "云网关管理")
@Slf4j
@Validated
@RestController
@RequestMapping("/cloud-gateway")
public class CloudGatewayController {

    @Resource
    private TrdPlatformInfoService trdPlatformInfoService;

    @Resource
    private CloudGatewayVoConverter cloudGatewayVoConverter;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


//    @Operation(summary = "云网关新增")
//    @PostMapping("/save")
//    public SingleResponse<?> create(@Valid @RequestBody CloudGatewayAddVo cloudGatewayAddVo,
//                                    @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
//        if (trdPlatformInfoService.isExistName(cloudGatewayAddVo.getCloudGatewayName())) {
//            return SingleResponse.buildFailure("10001", "名称已存在，请换一个试试");
//        }
//        if (trdPlatformInfoService.isExistCode(cloudGatewayAddVo.getCloudGatewayCode())) {
//            return SingleResponse.buildFailure("10002", "Code已存在，请换一个试试");
//        }
//        TrdPlatformInfoEntity entity = cloudGatewayAddVo.createEntity(bladeAuth);
//        if(trdPlatformInfoService.save(entity)){
//            return SingleResponse.buildSuccess(cloudGatewayVoConverter.fromTrdPlatformInfoEntity(entity));
//        }else{
//            return SingleResponse.buildFailure("10003", "保存失败");
//        }
//    }
//
//
//
//    @Operation(summary = "云网关编辑")
//    @PutMapping("/{id}")
//    public SingleResponse<?> update(@PathVariable Long id,
//                                    @Valid @RequestBody CloudGatewayUpdateVo cloudGatewayUpdateVo,
//                                    @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
//        return SingleResponse.buildSuccess(trdPlatformInfoService.updateById(cloudGatewayUpdateVo.createEntity(id, bladeAuth)));
//    }
//
//
//
//
//    @Operation(summary = "网关详情查询")
//    @GetMapping("/{id}")
//    public SingleResponse<CloudGatewayVo> get(@PathVariable Long id){
//        TrdPlatformInfoEntity entity = trdPlatformInfoService.getById(id);
//        return SingleResponse.buildSuccess(cloudGatewayVoConverter.fromTrdPlatformInfoEntity(entity));
//    }


    @Operation(summary = "网关Modbus配置编辑")
    @PutMapping("/{id}/config")
    public SingleResponse<?> editModbusConfig(@PathVariable Long id,
                                              @RequestBody CloudGatewayModbusConfigVo modbusConfigVo
                                              ){
        return SingleResponse.buildSuccess(trdPlatformInfoService.updateConfig(id, JSON.toJSONString(modbusConfigVo)));
    }
}
