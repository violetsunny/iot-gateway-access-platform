/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.DeviceSessionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: DeviceSessionMapper, v 0.1 2023/2/23 15:25 kanglele Exp $
 */
@Mapper
public interface DeviceSessionMapper extends BaseMapper<DeviceSessionEntity> {

    /**
     * 分页查询
     *
     * @param page
     * @param params
     * @return
     */
    IPage<DeviceSessionEntity> queryPage(IPage<DeviceSessionEntity> page, @Param("params") Map<String, Object> params);

    int remove(@Param("sessionId") String sessionId);

    DeviceSessionEntity query(@Param("sessionId") String sessionId);

    List<DeviceSessionEntity> queryServerId(@Param("serverId") String serverId);

    void removeServerId(String serverId);

}
