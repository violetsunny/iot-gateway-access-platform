/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.session.service.impl;

import com.ennew.iot.gateway.biz.session.service.DeviceSessionService;
import com.ennew.iot.gateway.common.constants.RedisConstant;
import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.bo.SessionPageQueryBo;
import com.ennew.iot.gateway.core.repository.DeviceSessionRepository;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import com.ennew.iot.gateway.integration.device.model.DeviceStateReq;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.kdla.framework.common.aspect.watch.StopWatchWrapper;
import top.kdla.framework.common.help.KdlaStringHelp;
import top.kdla.framework.dto.PageResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author kanglele
 * @version $Id: DeviceSessionService, v 0.1 2023/2/23 17:18 kanglele Exp $
 */
@Service
@Slf4j
public class DeviceSessionServiceImpl implements DeviceSessionService {

    @Resource
    private DeviceSessionRepository deviceSessionRepository;
    @Resource
    private RedisService redisService;
    @Resource
    private DeviceClient deviceClient;

    /**
     * 分页查询
     *
     * @param queryBo
     * @return
     */
    @Override
    public PageResponse<DeviceSessionBo> page(SessionPageQueryBo queryBo) {
        return deviceSessionRepository.queryPage(queryBo);
    }

    /**
     * 删除
     *
     * @param sessionId
     * @return
     */
    @Override
    @StopWatchWrapper(logHead = "DeviceSession", msg = "删除session")
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(String sessionId) {
        DeviceSessionBo deviceSessionBo = session(sessionId);
        if (Objects.isNull(deviceSessionBo)) {
            return Boolean.FALSE;
        }
        //删除数据
        deviceSessionRepository.remove(sessionId);
        redisService.hashIncrement(RedisConstant.DEVICE_SESSION_KEY, RedisConstant.USER_COUNT, -1);
        //通知物模型
        notifyDevice(Lists.newArrayList(deviceSessionBo));
        return Boolean.TRUE;
    }

    /**
     * 删除服务节点下的所有session
     *
     * @param serverId
     * @return
     */
    @Override
    @StopWatchWrapper(logHead = "DeviceSession", msg = "删除服务节点下的所有session")
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeServerId(String serverId) {
        List<DeviceSessionBo> bos = deviceSessionRepository.queryServerId(serverId);
        if (CollectionUtils.isEmpty(bos)) {
            return Boolean.FALSE;
        }
        deviceSessionRepository.removeServerId(serverId);
        redisService.hashIncrement(RedisConstant.DEVICE_SESSION_KEY, RedisConstant.USER_COUNT, -bos.size());
        //通知物模型设备端
        notifyDevice(bos);
        return Boolean.TRUE;
    }

    private void notifyDevice(List<DeviceSessionBo> bos) {
        bos.stream().map(d -> {
            String deviceId = d.getDeviceId();
            String state = redisService.getSateFromRedis(deviceId);
            state = KdlaStringHelp.isEmpty(state) ? "-1" : state;
            String deviceType = redisService.getDeviceTypeFromRedis(deviceId);
            deviceType = KdlaStringHelp.isEmpty(deviceType) ? "gateway" : deviceType;
            //t1=状态，t2=deviceId，t3=设备类型, gateway-网关,dtu,device-直连设备,childrenDevice-子设备,other-其他,plc,scada,thirdCloud-三方云
            return Triple.of(Integer.parseInt(state), deviceId, deviceType);
        }).collect(Collectors.groupingBy(Triple::getLeft))
                .entrySet().stream()
                .peek(entry -> {
                    List<String> devices = entry.getValue().stream().map(Triple::getMiddle).collect(Collectors.toList());
                    deviceClient.batchUpdateDeviceState(DeviceStateReq.of(devices, entry.getKey(), "1"));
                    List<String> deviceGateways = entry.getValue().stream().filter(t -> t.getRight().equals("gateway")).map(Triple::getMiddle).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deviceGateways)) {
                        //如果是网关设备则还通知子设备
                        deviceClient.batchUpdateDeviceState(DeviceStateReq.of(deviceGateways, entry.getKey(), "2"));
                    }
                }).collect(Collectors.toList());
    }

    /**
     * 查询
     *
     * @param sessionId
     * @return
     */
    @Override
    public DeviceSessionBo session(String sessionId) {
        return deviceSessionRepository.query(sessionId);
    }

    /**
     * 存储
     *
     * @param bo
     * @return
     */
    @Override
    @StopWatchWrapper(logHead = "DeviceSession", msg = "存储session")
    @Transactional(rollbackFor = Exception.class)
    public Boolean store(DeviceSessionBo bo) {
        // 增加用户数
        redisService.hashIncrement(RedisConstant.DEVICE_SESSION_KEY, RedisConstant.USER_COUNT, 1);
        return deviceSessionRepository.store(bo);
    }
}
