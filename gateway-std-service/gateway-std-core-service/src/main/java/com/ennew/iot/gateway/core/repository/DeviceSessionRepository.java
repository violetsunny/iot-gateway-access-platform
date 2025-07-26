/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.bo.SessionPageQueryBo;
import com.ennew.iot.gateway.core.converter.DeviceSessionBoConverter;
import com.ennew.iot.gateway.dal.entity.DeviceSessionEntity;
import com.ennew.iot.gateway.dal.mapper.DeviceSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.kdla.framework.common.enums.IsDeletedEnum;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: DeviceSessionRepository, v 0.1 2023/2/23 15:42 kanglele Exp $
 */
@Repository
@Slf4j
public class DeviceSessionRepository extends ServiceImpl<DeviceSessionMapper, DeviceSessionEntity> implements IService<DeviceSessionEntity> {

    @Resource
    private DeviceSessionBoConverter deviceSessionBoConverter;

    /**
     * 存储
     *
     * @param bo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean store(DeviceSessionBo bo) {
        DeviceSessionEntity entity = deviceSessionBoConverter.fromDeviceSession(bo);
        return this.saveOrUpdate(entity);
    }

    /**
     * 去除session
     * @param sessionId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean remove(String sessionId) {
        return baseMapper.remove(sessionId) > 1;
    }


    /**
     * 分页查询
     * @param pageQuery
     * @return
     */
    public PageResponse<DeviceSessionBo> queryPage(SessionPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<DeviceSessionEntity> page = baseMapper.queryPage(new PlusPageQuery<DeviceSessionEntity>(pageQuery).getPage(params), params);
        List<DeviceSessionBo> list = deviceSessionBoConverter.toDeviceSessions(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    /**
     * 查询
     * @param sessionId
     * @return
     */
    public DeviceSessionBo query(String sessionId) {
        DeviceSessionEntity entity = baseMapper.query(sessionId);
        return deviceSessionBoConverter.toDeviceSession(entity);
    }

    /**
     * 按服务查找session
     * @param serverId
     * @return
     */
    public List<DeviceSessionBo> queryServerId(String serverId) {
        List<DeviceSessionEntity> entitys = baseMapper.queryServerId(serverId);
        return deviceSessionBoConverter.toDeviceSessions(entitys);
    }

    /**
     * 按服务删除session
     * @param serverId
     */
    public void removeServerId(String serverId) {
        baseMapper.removeServerId(serverId);
    }
}
