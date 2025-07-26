package com.ennew.iot.gateway.biz.cloudgateway.impl;

import cn.enncloud.iot.gateway.exception.BizException;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayDeviceService;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceMetricBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDevicePageQueryBO;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import com.ennew.iot.gateway.dal.mapper.CloudGatewayDeviceMapper;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformInfoMapper;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import com.ennew.iot.gateway.integration.device.model.DeviceDataBatchReq;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.ennew.iot.gateway.integration.device.model.ProjectDataRes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CloudGatewayDeviceServiceImpl extends ServiceImpl<CloudGatewayDeviceMapper, CloudGatewayDeviceEntity> implements CloudGatewayDeviceService {


    @Resource
    private TrdPlatformInfoMapper trdPlatformInfoMapper;


    @Resource
    private CloudGatewayDeviceMapper cloudGatewayDeviceMapper;

    @Resource
    private DeviceClient deviceClient;

    @Resource
    private RedisService redisService;

    @Override
    public List<String> exists(String gatewayCode, List<String> deviceIdList) {
        return cloudGatewayDeviceMapper.selectList(Wrappers.<CloudGatewayDeviceEntity>query()
                .lambda()
                        .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
                .in(CloudGatewayDeviceEntity::getDeviceId, deviceIdList)
        ).stream()
                .map(CloudGatewayDeviceEntity::getDeviceId)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean bindDevice(String gatewayCode, Collection<CloudGatewayDeviceEntity> entityList) {
        Set<String> aldreayBindDeviceSet = cloudGatewayDeviceMapper.selectList(Wrappers.<CloudGatewayDeviceEntity>query()
                        .lambda()
                        .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
                )
                .stream()
                .map(CloudGatewayDeviceEntity::getDeviceId)
                .collect(Collectors.toSet());
        List<CloudGatewayDeviceEntity> unBindDeviceList = entityList.stream()
                .filter(e -> !aldreayBindDeviceSet.contains(e.getDeviceId()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(unBindDeviceList)){
            return true;
        }
        if (unBindDeviceList.size() == 1) {
            CloudGatewayDeviceEntity entity = entityList.iterator().next();
            return save(entity);
        }
        return saveBatch(entityList);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unbindDevice(String gatewayCode, List<String> deviceIdList) {
        if (deviceIdList == null) {
            return false;
        }
        deviceIdList.forEach(deviceId -> {
            cloudGatewayDeviceMapper.delete(
                    Wrappers.<CloudGatewayDeviceEntity>query()
                            .lambda()
                            .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
                            .eq(CloudGatewayDeviceEntity::getDeviceId, deviceId));
        });
        return true;
    }

    @Override
    public PageResponse<CloudGatewayDeviceBO> queryPage(String gatewayCode, CloudGatewayDevicePageQueryBO queryBO) {
        Page<CloudGatewayDeviceEntity> page = cloudGatewayDeviceMapper.selectPage(new Page<>(queryBO.getPageNum(), queryBO.getPageSize()),
                Wrappers.<CloudGatewayDeviceEntity>query()
                        .lambda()
                        .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
                        .orderByDesc(CloudGatewayDeviceEntity::getDeviceId)
        );
        List<String> deviceIdList = page.getRecords().stream()
                .map(CloudGatewayDeviceEntity::getDeviceId)
                .collect(Collectors.toList());
        List<CloudGatewayDeviceBO> cloudGatewayDeviceBOList = queryByDeviceClient(gatewayCode, deviceIdList);
        return PageResponse.of(cloudGatewayDeviceBOList, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<CloudGatewayDeviceBO> queryList(String gatewayCode) {
        List<String> deviceIds = cloudGatewayDeviceMapper.selectList(Wrappers.<CloudGatewayDeviceEntity>query()
                        .lambda()
                        .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
                        .orderByDesc(CloudGatewayDeviceEntity::getDeviceId)
                ).stream()
                .map(CloudGatewayDeviceEntity::getDeviceId)
                .collect(Collectors.toList());
        return queryByDeviceClient(gatewayCode, deviceIds);
    }

    @Override
    public Long queryDeviceCount(String gatewayCode) {
        return cloudGatewayDeviceMapper.selectCount(Wrappers.<CloudGatewayDeviceEntity>query()
                .lambda()
                .eq(CloudGatewayDeviceEntity::getCloudGatewayCode, gatewayCode)
        );
    }

    @Override
    public List<CloudGatewayDeviceMetricBO> queryDeviceMetricList(String productId, String deviceId) {
        JSONArray array = redisService.getMeasurePsFromRedisByProduct(productId);
        if(array == null || array.isEmpty()){
            return Collections.emptyList();
        }
        return array.stream()
                .map(o -> (JSONObject)o)
                .map(json -> {
                    CloudGatewayDeviceMetricBO deviceMetricBO = new CloudGatewayDeviceMetricBO();
                    deviceMetricBO.setDeviceMetric(json.getString("code"));
                    deviceMetricBO.setDeviceMetricName(json.getString("name"));
                    deviceMetricBO.setDeviceId(deviceId);
                    return deviceMetricBO;
                })
                .collect(Collectors.toList());
    }


    private List<CloudGatewayDeviceBO> queryByDeviceClient(String gatewayCode, List<String> deviceIds){
        if(CollectionUtils.isEmpty(deviceIds)){
            return Collections.emptyList();
        }
        DeviceDataBatchReq req = new DeviceDataBatchReq();
        req.setDeviceIds(deviceIds);
        req.setTagQueryFlag(false);
        MultiResponse<DeviceDataRes> response = deviceClient.listByDeviceIds(req);
        if(!response.isSuccess()){
            throw new BizException("设备列表查询失败，" + response.getMsg());
        }
        Collection<DeviceDataRes> data = response.getData();
        if(CollectionUtils.isEmpty(data)){
            throw new BizException("未查询到设备信息");
        }
        List<String> projectIds = data.stream()
                .map(DeviceDataRes::getProjectId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProjectDataRes> projectMapper = queryProjects(projectIds);
        return data.stream()
                .map(res -> newCloudGatewayDeviceBO(gatewayCode, res, projectMapper))
                .collect(Collectors.toList());
    }


    private CloudGatewayDeviceBO newCloudGatewayDeviceBO(String gatewayCode, DeviceDataRes res, Map<String, ProjectDataRes> projectMapper){
        CloudGatewayDeviceBO bo = new CloudGatewayDeviceBO();
        bo.setGatewayCode(gatewayCode);
        bo.setDeviceId(res.getId());
        bo.setDeviceName(res.getName());
        bo.setProductId(res.getProductId());
        bo.setProductName(res.getProductName());
        bo.setEntityTypeId(res.getEntityTypeId());
        bo.setEntityTypeName(res.getEntityTypeName());
        bo.setProjectId(res.getProjectId());
        ProjectDataRes projectDataRes = projectMapper.get(res.getProjectId());
        if(projectDataRes != null){
            bo.setProjectName(projectDataRes.getProjectName());
        }
        return bo;
    }


    private Map<String, ProjectDataRes> queryProjects(List<String> projectIds){
        Map<String, ProjectDataRes> map = new HashMap<>();
        MultiResponse<ProjectDataRes> res = deviceClient.getSimpleProjectListByIds(projectIds);
        if(!res.isSuccess()){
            return map;
        }
        Collection<ProjectDataRes> data = res.getData();
        if(CollectionUtils.isEmpty(data)){
            return map;
        }
        data.forEach(p -> map.put(p.getId(), p));
        return map;
    }

}
