package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @description TrdPlatformModelRefMapper
 * @author qk
 * @date 2024-04-08
 */
public interface TrdPlatformMeasureRefMapper extends BaseMapper<TrdPlatformMeasureRefEntity> {

    IPage<TrdPlatformMeasureRefEntity> queryPage(IPage<TrdPlatformMeasureRefEntity> page, @Param("params") Map<String, Object> params);

}