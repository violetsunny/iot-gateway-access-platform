package com.ennew.iot.gateway.biz.cloudgateway.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointMappingService;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingPageQueryBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointMappingBO;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import com.ennew.iot.gateway.dal.mapper.CloudGatewayPointMappingMapper;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import com.ennew.iot.gateway.integration.device.model.DeviceDataBatchReq;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.ennew.iot.gateway.integration.device.model.DeviceStateReq;
import org.springframework.stereotype.Service;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CloudGatewayPointMappingServiceImpl extends ServiceImpl<CloudGatewayPointMappingMapper, CloudGatewayPointMappingEntity>
        implements CloudGatewayPointMappingService {


    @Resource
    private CloudGatewayPointMappingMapper cloudGatewayPointMappingMapper;


    @Resource
    private RedisService redisService;


    @Resource
    private DeviceClient deviceClient;

    @Override
    public boolean exists(CloudGatewayPointMappingEntity pointMappingEntity) {
        return cloudGatewayPointMappingMapper.exists(Wrappers.<CloudGatewayPointMappingEntity>query()
                .lambda()
                .eq(CloudGatewayPointMappingEntity::getCloudGatewayCode, pointMappingEntity.getCloudGatewayCode())
                .eq(CloudGatewayPointMappingEntity::getDeviceId, pointMappingEntity.getDeviceId())
                //.eq(CloudGatewayPointMappingEntity::getPointId, pointMappingEntity.getPointId())
                .eq(CloudGatewayPointMappingEntity::getMetric, pointMappingEntity.getMetric()));
    }

    @Override
    public List<CloudGatewayPointMappingBO> getPointMapping(String gatewayCode) {
        // JSONArray measurePsFromRedisByProduct = redisService.getMeasurePsFromRedisByProduct("1672897736829431810");

        List<CloudGatewayPointMappingEntity> pointMappingList = cloudGatewayPointMappingMapper.selectList(Wrappers.<CloudGatewayPointMappingEntity>query()
                .lambda()
                .eq(CloudGatewayPointMappingEntity::getCloudGatewayCode, gatewayCode));
        if(pointMappingList == null || pointMappingList.isEmpty()){
            return Collections.emptyList();
        }
        return pointMappingList.stream()
                .map(pm -> {
                    CloudGatewayPointMappingBO mappingBO = new CloudGatewayPointMappingBO();
                    mappingBO.setDeviceId(pm.getDeviceId());
                    mappingBO.setMetric(pm.getMetric());
                    mappingBO.setPointId(pm.getPointId());
                    mappingBO.setProductId(pm.getProductId());
                    return mappingBO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean unbind(String gatewayCode, Long pointId) {
        Integer result = cloudGatewayPointMappingMapper.delete(Wrappers.<CloudGatewayPointMappingEntity>query()
                .lambda()
                .eq(CloudGatewayPointMappingEntity::getPointId, pointId)
                .eq(CloudGatewayPointMappingEntity::getCloudGatewayCode, gatewayCode)
        );
        return SqlHelper.retBool(result);
    }

    @Override
    public PageResponse<CloudGatewayModbusMappingBO> queryModbusMappingPage(String gatewayCode, CloudGatewayModbusMappingPageQueryBO queryBO) {
        Page<Map<String, Object>> page = Page.of(queryBO.getPageNum(), queryBO.getPageSize());
        Page<Map<String, Object>> queryPage = cloudGatewayPointMappingMapper.queryMappingPage(page, gatewayCode, queryBO.getStatus(), queryBO.getDeviceId());
        Set<String> productIdList = new HashSet<>();
        Map<String, JSONObject> metricMap = new HashMap<>();
        Set<String> deviceIdSet = new HashSet<>();
        List<CloudGatewayModbusMappingBO> boList = queryPage.getRecords()
                .stream()
                .map(m -> {
                    String configJson = (String) m.get("configJson");
                    JSONObject config = configJson == null ? new JSONObject() : JSON.parseObject(configJson);
                    CloudGatewayModbusMappingBO mappingBO = new CloudGatewayModbusMappingBO();
                    mappingBO.setPointName((String) m.get("pointName"));
                    mappingBO.setPointId((Long) m.get("pointId"));
                    mappingBO.setPointRealDeviceName((String) m.get("realDeviceName"));
                    mappingBO.setPointByteOrder(config.getString("byteOrder"));
                    mappingBO.setPointDataType(config.getString("dataType"));
                    mappingBO.setPointRegisterAddress(config.getString("registerAddress"));
                    mappingBO.setPointFunctionCode(config.getString("functionCode"));
                    mappingBO.setBindDevice(false);
                    String deviceId = (String) m.get("deviceId");
                    if (deviceId != null) {
                        deviceIdSet.add(deviceId);
                        mappingBO.setDeviceId(deviceId);
                        String productId = (String) m.get("productId");
                        if (productId != null) {
                            productIdList.add(productId);
                        }
                        mappingBO.setProductId(productId);
                        mappingBO.setDeviceMetric((String) m.get("deviceMetric"));
                        mappingBO.setBindDevice(true);
                    }
                    return mappingBO;
                })
                .collect(Collectors.toList());
        productIdList.forEach(pid -> {
            JSONArray array = redisService.getMeasurePsFromRedisByProduct(pid);
            if (array == null || array.isEmpty()) {
                return;
            }
            array.forEach(o -> {
                JSONObject jsonObject = (JSONObject) o;
                String code = jsonObject.getString("code");
                if (code == null) {
                    return;
                }
                metricMap.put(pid + "_" + code, jsonObject);
            });
        });
        Map<String, String> deviceNameMap = new HashMap<>();
        DeviceDataBatchReq req = new DeviceDataBatchReq();
        req.setDeviceIds(new ArrayList<>(deviceIdSet));
        req.setTagQueryFlag(false);
        MultiResponse<DeviceDataRes> response = deviceClient.listByDeviceIds(req);
        if (response.isSuccess() && response.getData() != null) {
            response.getData()
                    .forEach(r -> deviceNameMap.put(r.getId(), r.getName()));
        }

        boList.forEach(bo -> {
            if (!bo.isBindDevice()) {
                return;
            }
            String deviceMetric = bo.getDeviceMetric();
            String productId = bo.getProductId();
            JSONObject jsonObject = metricMap.get(productId + "_" + deviceMetric);
            if (jsonObject == null) {
                return;
            }
            bo.setDeviceName(deviceNameMap.get(bo.getDeviceId()));
            bo.setDeviceMetricDataType(jsonObject.getString("type"));
            bo.setDeviceMetricReadOnly(jsonObject.getBoolean("readOnly"));
            bo.setDeviceMetricUnit(jsonObject.getString("unit"));
            bo.setDeviceMetricName(jsonObject.getString("name"));
        });
        return PageResponse.of(boList, queryPage.getTotal(), queryPage.getSize(), queryPage.getCurrent());
    }

    @Override
    public boolean isBind(String gatewayCode, Long pointId) {
        return cloudGatewayPointMappingMapper.exists(Wrappers.<CloudGatewayPointMappingEntity>query()
                .lambda()
                .eq(CloudGatewayPointMappingEntity::getCloudGatewayCode, gatewayCode)
                .eq(CloudGatewayPointMappingEntity::getPointId, pointId));
    }


    private String getMapString(Map<String, Object> map, String key) {
        Object o = map.get(key);
        if (o == null) {
            return null;
        }
        return String.valueOf(o);
    }
}
