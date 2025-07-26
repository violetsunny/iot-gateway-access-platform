package com.ennew.iot.gateway.biz.cloudgateway.impl;

import cn.enncloud.iot.gateway.exception.BizException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayDeviceService;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointMappingService;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointService;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.converter.CloudGatewayPointBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.enums.PlatformTypeEnum;
import com.ennew.iot.gateway.dal.mapper.CloudGatewayPointMapper;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformInfoMapper;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import com.ennew.iot.gateway.integration.device.model.DeviceDataBatchReq;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageQuery;
import top.kdla.framework.dto.PageResponse;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CloudGatewayPointServiceImpl extends ServiceImpl<CloudGatewayPointMapper, CloudGatewayPointEntity> implements CloudGatewayPointService {

    private static final int DEVICE_CLIENT_BATCH_SIZE = 100;

    @Resource
    private TrdPlatformInfoMapper platformInfoMapper;

    @Resource
    private CloudGatewayPointMapper cloudGatewayPointMapper;

    @Resource
    private CloudGatewayPointBoConverter cloudGatewayPointBoConverter;

    @Resource
    private CloudGatewayDeviceService cloudGatewayDeviceService;


    @Resource
    private CloudGatewayPointMappingService cloudGatewayPointMappingService;

    @Resource
    private DeviceClient deviceClient;

    @Resource
    private ExecutorService executorService;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public boolean exists(CloudGatewayPointEntity point) {
        return cloudGatewayPointMapper.exists(
                Wrappers.<CloudGatewayPointEntity>query()
                        .lambda()
                        .eq(CloudGatewayPointEntity::getCloudGatewayCode, point.getCloudGatewayCode())
                        .eq(CloudGatewayPointEntity::getName, point.getName()));
    }

    @Override
    public boolean exists(Long pointId) {
        return cloudGatewayPointMapper.exists(
                Wrappers.<CloudGatewayPointEntity>query()
                        .lambda()
                        .eq(CloudGatewayPointEntity::getId, pointId));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePoint(CloudGatewayPointEntity point) {
        String gatewayCode = point.getCloudGatewayCode();
        TrdPlatformInfoEntity platformInfo = queryCloudGateway(gatewayCode);
        if (platformInfo == null) {
            log.warn("gateway not found, pCode={}", gatewayCode);
            return false;
        }
        Integer pType = platformInfo.getPType();
        point.setConnectorType(pType);
        return save(point);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean savePoints(String gatewayCode, List<CloudGatewayPointEntity> points) {
        if (points == null) {
            return false;
        }
        if (points.size() == 1) {
            return savePoint(points.get(0));
        }
        TrdPlatformInfoEntity platformInfo = queryCloudGateway(gatewayCode);
        if (platformInfo == null) {
            log.warn("gateway not found, pCode={}", gatewayCode);
            return false;
        }
        Integer pType = platformInfo.getPType();
        points.forEach(p -> p.setConnectorType(pType));
        return saveBatch(points);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExcelImportErrorBO importModbusPoints(String gatewayCode, List<CloudGatewayModbusPointImportBO> importBOList) {
        ExcelImportErrorBO result = new ExcelImportErrorBO();
        Set<String> existsPointNameSet = cloudGatewayPointMapper.selectList(Wrappers.<CloudGatewayPointEntity>query()
                        .lambda()
                        .eq(CloudGatewayPointEntity::getCloudGatewayCode, gatewayCode))
                .stream()
                .map(CloudGatewayPointEntity::getName)
                .collect(Collectors.toSet());
        // step1 根据是否包含产品ID、设备ID、设备测点信息将导入测点分组
        Map<Boolean, List<CloudGatewayModbusPointImportBO>> importBoGroup = importBOList
                .stream()
                .filter(bo -> {
                    boolean exists = existsPointNameSet.contains(bo.getPointName());
                    if (exists) {
                        result.addError(bo.getRow(), "原始测点已存在");
                    }
                    return !exists;
                })
                .collect(Collectors.groupingBy(bo ->
                        StringUtils.hasText(bo.getProductId()) &&
                                StringUtils.hasText(bo.getDeviceId()) &&
                                StringUtils.hasText(bo.getDeviceMetric())
                ));
        List<CloudGatewayModbusPointImportBO> importWithMappingBOList = importBoGroup.get(Boolean.TRUE);
        List<CloudGatewayModbusPointImportBO> importWithoutMappingBOList = importBoGroup.get(Boolean.FALSE);
        if (!CollectionUtils.isEmpty(importWithMappingBOList)) {
            importGatewayPointWithDeviceMetric(gatewayCode, importWithMappingBOList, result);
        }
        if (result.hasError()) {
            return result;
        }
        // step3 保存不需要添加设备测点映射关系的原始点位
        if (CollectionUtils.isEmpty(importWithoutMappingBOList)) {
            return result;
        }
        List<CloudGatewayPointEntity> withoutMappingGatewayPointList = importWithoutMappingBOList
                .stream()
                .map(CloudGatewayModbusPointImportBO::createCloudGatewayPointEntity)
                .collect(Collectors.toList());
        savePoints(gatewayCode, withoutMappingGatewayPointList);
        return result;
    }

    @Override
    public PageResponse<CloudGatewayPointBO> queryPage(String gatewayCode, CloudGatewayPointPageQueryBO pageQueryBO) {
        LambdaQueryWrapper<CloudGatewayPointEntity> wrapper = Wrappers.<CloudGatewayPointEntity>query().lambda()
                .eq(CloudGatewayPointEntity::getCloudGatewayCode, gatewayCode);
        if (StringUtils.hasText(pageQueryBO.getPointName())) {
            wrapper.like(CloudGatewayPointEntity::getName, pageQueryBO.getPointName());
        }
        if (StringUtils.hasText(pageQueryBO.getRealDeviceName())) {
            wrapper.like(CloudGatewayPointEntity::getRealDeviceName, pageQueryBO.getRealDeviceName());
        }
        wrapper.orderBy(true,
                PageQuery.ASC.equals(pageQueryBO.getOrderDirection()), CloudGatewayPointEntity::getSort);
        Page<CloudGatewayPointEntity> queryPage = new Page<>(pageQueryBO.getPageNum(), pageQueryBO.getPageSize());
        Page<CloudGatewayPointEntity> page = cloudGatewayPointMapper.selectPage(queryPage, wrapper);
        List<CloudGatewayPointBO> cloudGatewayPointBOList = cloudGatewayPointBoConverter.toCloudGatewayPointBOCollection(page.getRecords());
        return PageResponse.of(cloudGatewayPointBOList, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<? extends CloudGatewayPointBO> getPoints(String gatewayCode) {
        TrdPlatformInfoEntity platformInfo = platformInfoMapper.selectOne(Wrappers.<TrdPlatformInfoEntity>query()
                .lambda()
                .eq(TrdPlatformInfoEntity::getPCode, gatewayCode));
        if (platformInfo == null) {
            log.warn("gateway not found, pCode={}", gatewayCode);
            return Collections.emptyList();
        }
        PlatformTypeEnum platformType = PlatformTypeEnum.parse(platformInfo.getPType());
        List<? extends CloudGatewayPointBO> result = null;
        switch (platformType) {
            case MODBUS_TCP:
                result = getModbusPoints(gatewayCode);
                break;
            case REQUEST:
            case CTWING:
            default:
                break;
        }
        return result;
    }


    @Override
    public List<CloudGatewayModbusPointBO> getModbusPoints(String gatewayCode) {
        TrdPlatformInfoEntity platformInfo = platformInfoMapper.selectOne(Wrappers.<TrdPlatformInfoEntity>query()
                .lambda()
                .eq(TrdPlatformInfoEntity::getPCode, gatewayCode));
        if (platformInfo == null) {
            log.warn("gateway not found, pCode={}", gatewayCode);
            return Collections.emptyList();
        }
        // 查询网关下所有点位信息
        List<CloudGatewayPointEntity> cloudGatewayPoints = cloudGatewayPointMapper.selectList(
                Wrappers.<CloudGatewayPointEntity>query()
                        .lambda()
                        .eq(CloudGatewayPointEntity::getCloudGatewayCode, gatewayCode)
                        .orderByAsc(CloudGatewayPointEntity::getSort));
        if (cloudGatewayPoints == null || cloudGatewayPoints.isEmpty()) {
            return Collections.emptyList();
        }
        String configJson = platformInfo.getConfigJson();
        JSONObject parse = new JSONObject();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(configJson)) {
            parse = JSON.parseObject(configJson);
        }
        Integer slaveAddr = Optional.ofNullable(parse.getInteger("salveAddress")).orElse(1);
        // 将点位信息转化为网关所需格式
        return cloudGatewayPoints.stream()
                .map(p -> {
                    CloudGatewayModbusPointBO modbusPointBO = JSON.parseObject(p.getConfigJson(), CloudGatewayModbusPointBO.class);
                    modbusPointBO.setRealDeviceName(p.getRealDeviceName());
                    modbusPointBO.setSlaveAddress(slaveAddr);
                    modbusPointBO.setPointId(p.getId());
                    modbusPointBO.setSort(p.getSort());
                    modbusPointBO.setName(p.getName());
                    modbusPointBO.setConnectorType(p.getConnectorType());
                    modbusPointBO.setCloudGatewayCode(p.getCloudGatewayCode());
                    modbusPointBO.setRemark(p.getRemark());
                    return modbusPointBO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long queryPointCount(String gatewayCode) {
        return cloudGatewayPointMapper.selectCount(Wrappers.<CloudGatewayPointEntity>query()
                .lambda()
                .eq(CloudGatewayPointEntity::getCloudGatewayCode, gatewayCode)
        );
    }


    private void importGatewayPointWithDeviceMetric(String gatewayCode, List<CloudGatewayModbusPointImportBO> importWithMappingBOList, ExcelImportErrorBO result) {
        // step2.1 校验设备, 异步查询设备信息
        Map<String, CloudGatewayModbusPointValidateBO> deviceValidateMap = requestDeviceProductMap(importWithMappingBOList);
        // step2.2 导入包含映射设备测点记录
        // 批量查询物模型量测属性
        List<String> entityTypeCodes = deviceValidateMap.values()
                .stream()
                .map(CloudGatewayModbusPointValidateBO::getEntityTypeCode)
                .distinct()
                .collect(Collectors.toList());
        Map<String, Set<String>> entityMetricMap = queryEntityType(entityTypeCodes);
        List<CloudGatewayPointEntity> pointEntityList = new ArrayList<>();
        Map<String, CloudGatewayDeviceEntity> gatewayDeviceEntityMap = new HashMap<>();
        List<CloudGatewayPointMappingEntity> pointMappingEntityList = new ArrayList<>();
        importWithMappingBOList.forEach(bo -> {
            CloudGatewayModbusPointValidateBO validateBO = deviceValidateMap.get(bo.getDeviceId());
            // 校验设备是否存在
            if (validateBO == null) {
                result.addError(bo.getRow(), "设备ID不存在");
                return;
            }
            // 校验产品是否一致
            if (!validateBO.productEqual(bo.getProductId())) {
                result.addError(bo.getRow(), "产品ID不匹配");
                return;
            }
            // 校验量测属性是否存在
            if (!validateBO.metricExits(bo.getDeviceMetric(), entityMetricMap)) {
                result.addError(bo.getRow(), "量测属性不匹配");
                return;
            }
            CloudGatewayPointEntity point = bo.createCloudGatewayPointEntity();
            CloudGatewayDeviceEntity device = bo.createCloudGatewayDeviceEntity();
            CloudGatewayPointMappingEntity mapping = bo.createCloudGatewayPointMappingEntity();
            pointEntityList.add(point);
            gatewayDeviceEntityMap.put(bo.getDeviceId(), device);
            pointMappingEntityList.add(mapping);
        });
        if (result.hasError()) {
            return;
        }
        savePoints(gatewayCode, pointEntityList);
        // step2.3 添加关联设备
        cloudGatewayDeviceService.bindDevice(gatewayCode, gatewayDeviceEntityMap.values());
        // step2.4 添加映射关系
        for (int i = 0; i < pointMappingEntityList.size(); i++) {
            pointMappingEntityList.get(i).setPointId(pointEntityList.get(i).getId());
        }
        cloudGatewayPointMappingService.saveBatch(pointMappingEntityList);
    }


    private TrdPlatformInfoEntity queryCloudGateway(String gatewayCode) {
        return platformInfoMapper.selectOne(Wrappers.<TrdPlatformInfoEntity>query()
                .lambda()
                .eq(TrdPlatformInfoEntity::getPCode, gatewayCode));
    }


    private Map<String, CloudGatewayModbusPointValidateBO> queryDevice(List<String> deviceIds) {
        DeviceDataBatchReq req = new DeviceDataBatchReq();
        req.setDeviceIds(deviceIds);
        req.setTagQueryFlag(Boolean.FALSE);
        MultiResponse<DeviceDataRes> response = deviceClient
                .listByDeviceIds(req);
        log.info("设备查询结果：" + JSON.toJSONString(response));
        if (!response.isSuccess()) {
            log.warn("device api query failed, request:{}, response:{}", req, response);
            return null;
        }
        Collection<DeviceDataRes> data = response.getData();
        if (data.isEmpty()) {
            return null;
        }
        Map<String, CloudGatewayModbusPointValidateBO> map = new HashMap<>(deviceIds.size());
        data.forEach(deviceDataRes -> {
            CloudGatewayModbusPointValidateBO bo = new CloudGatewayModbusPointValidateBO();
            bo.setProductId(deviceDataRes.getProductId());
            bo.setEntityCode(deviceDataRes.getEntityTypeCode());
            bo.setEntityTypeCode(deviceDataRes.getEntityTypeCode());
            map.put(deviceDataRes.getId(), bo);
        });
        return map;
    }


    private Map<String, CloudGatewayModbusPointValidateBO> requestDeviceProductMap(List<CloudGatewayModbusPointImportBO> importWithMappingBOList) {
        List<String> deviceIds = importWithMappingBOList.stream()
                .map(CloudGatewayModbusPointImportBO::getDeviceId)
                .distinct()
                .collect(Collectors.toList());
        List<List<String>> partition = Lists.partition(deviceIds, DEVICE_CLIENT_BATCH_SIZE);
        CountDownLatch countDownLatch = new CountDownLatch(partition.size());
        List<Future<Map<String, CloudGatewayModbusPointValidateBO>>> futures = new ArrayList<>();
        partition.forEach(ids -> {
            Future<Map<String, CloudGatewayModbusPointValidateBO>> future = executorService.submit(() -> {
                Map<String, CloudGatewayModbusPointValidateBO> map = queryDevice(deviceIds);
                countDownLatch.countDown();
                return map;
            });
            futures.add(future);
        });
        boolean allFinished = false;
        try {
            allFinished = countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new BizException("设备信息批量查询异常, " + e.getMessage());
        }
        if (!allFinished) {
            throw new BizException("设备信息查询超时");
        }
        Map<String, CloudGatewayModbusPointValidateBO> deviceIdProductCodeMap = new HashMap<>(deviceIds.size());
        futures.forEach(f -> {
            try {
                Map<String, CloudGatewayModbusPointValidateBO> map = f.get();
                if (map != null) {
                    deviceIdProductCodeMap.putAll(map);
                }
            } catch (Exception e) {
                throw new BizException("设备查询结果处理异常，" + e.getMessage());
            }
        });
        return deviceIdProductCodeMap;
    }


    public Map<String, Set<String>> queryEntityType(List<String> entityTypeCodes) {
        byte[] field = "measureProperties".getBytes(StandardCharsets.UTF_8);
        List<Object> objects = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            entityTypeCodes.forEach(id -> {
                String key = "entityType:custom:" + id;
                connection.hashCommands().hGet(key.getBytes(StandardCharsets.UTF_8), field);
            });
            return null;
        });
        Map<String, Set<String>> entityMetricMap = new HashMap<>();
        for (int i = 0; i < entityTypeCodes.size(); i++) {
            String entityTypeCode = entityTypeCodes.get(i);
            Object obj = objects.get(i);
            if (obj == null) {
                continue;
            }
            String metricsJson = obj.toString();
            JSONArray jsonArray = JSON.parseArray(metricsJson);
            if (jsonArray == null || jsonArray.isEmpty()) {
                continue;
            }
            Set<String> metrics = jsonArray.stream().map(item -> {
                JSONObject jsonObject = (JSONObject) item;
                return jsonObject.getString("code");
            }).collect(Collectors.toSet());
            entityMetricMap.put(entityTypeCode, metrics);
        }
        return entityMetricMap;
    }
}
