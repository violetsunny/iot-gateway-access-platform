package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.TrdPlatformTaskEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @description trd_platform_task
 * @author ruanhong
 * @date 2024-03-13
 */
public interface TrdPlatformTaskMapper extends BaseMapper<TrdPlatformTaskEntity> {

    IPage<TrdPlatformTaskEntity> queryPage(IPage<TrdPlatformTaskEntity> page, @Param("params") Map<String, Object> params);

}