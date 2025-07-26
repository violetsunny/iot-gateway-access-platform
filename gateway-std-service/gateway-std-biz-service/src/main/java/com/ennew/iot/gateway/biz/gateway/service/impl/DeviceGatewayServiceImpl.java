package com.ennew.iot.gateway.biz.gateway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ennew.iot.gateway.biz.gateway.DeviceGateway;
import com.ennew.iot.gateway.biz.gateway.service.DeviceGatewayService;
import com.ennew.iot.gateway.biz.gateway.supports.DeviceGatewayManager;
import com.ennew.iot.gateway.core.bo.DeviceGatewayBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayPageQueryBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.core.repository.DeviceGatewayRepository;
import com.ennew.iot.gateway.core.repository.NetworkConfigRepository;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.common.aspect.watch.StopWatchWrapper;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class DeviceGatewayServiceImpl implements DeviceGatewayService {

    @Autowired
    private DeviceGatewayRepository deviceGatewayRepository;

    @Autowired
    private DeviceGatewayManager deviceGatewayManager;

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "添加网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DeviceGatewayBo bo) {
        return deviceGatewayRepository.save(bo);
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "修改网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean update(DeviceGatewayBo bo) {
        return deviceGatewayRepository.update(bo);
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "根据id查询网关")
    public DeviceGatewayResBo getById(String id) {
        return deviceGatewayRepository.queryById(id);
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "网关分页查询")
    public PageResponse<DeviceGatewayResBo> queryPage(DeviceGatewayPageQueryBo queryPage) {
        return deviceGatewayRepository.queryPage(queryPage);
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "删除网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        return deviceGatewayRepository.delete(id);
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "启动网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean startup(String id) {
        deviceGatewayRepository.updateState(id, NetworkConfigState.enabled);
        DeviceGateway deviceGateway = deviceGatewayManager.getGateway(id);
        deviceGateway.startup();
        return true;
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "暂停网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean pause(String id) {
        deviceGatewayRepository.updateState(id, NetworkConfigState.paused);
        DeviceGateway deviceGateway = deviceGatewayManager.getGateway(id);
        deviceGateway.pause();
        return true;
    }

    @Override
    @StopWatchWrapper(logHead = "DeviceGateway", msg = "停止网关")
    @Transactional(rollbackFor = Exception.class)
    public boolean shutdown(String id) {
        deviceGatewayRepository.updateState(id, NetworkConfigState.disabled);
        DeviceGateway deviceGateway = deviceGatewayManager.getGateway(id);
        deviceGateway.shutdown();
        deviceGatewayManager.removeGatewayCache(id);
        return true;
    }

    @Autowired
    private NetworkConfigRepository networkConfigRepository;

    @Override
    public MultiResponse<HashMap<String, String>> listNetWorkConfig() {
        List<HashMap<String, String>> list = new ArrayList<>();
        LambdaQueryWrapper<DeviceGatewayEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceGatewayEntity::getState, "enabled");
        List<DeviceGatewayEntity> gatewayList = deviceGatewayRepository.list(queryWrapper);
        if (CollectionUtils.isEmpty(gatewayList)) {
            MultiResponse.buildSuccess(list);
        }
        gatewayList.forEach(gateway -> {
            NetworkConfigEntity networkConfigEntity = networkConfigRepository.getById(gateway.getNetworkId());
            if (networkConfigEntity != null && networkConfigEntity.getConfiguration() != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("host", String.valueOf(networkConfigEntity.getConfiguration().get("host")));
                map.put("port", String.valueOf(networkConfigEntity.getConfiguration().get("port")));
                list.add(map);
            }
        });
        return MultiResponse.buildSuccess(list);
    }

}
