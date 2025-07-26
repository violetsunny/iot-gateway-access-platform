package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.CloudDockingEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午3:30 2023/5/22
 */
public interface CloudDockingMapper extends BaseMapper<CloudDockingEntity> {

    IPage<CloudDockingEntity> queryPage(IPage<CloudDockingEntity> page, @Param("params") Map<String, Object> params);

    CloudDockingEntity queryById(@Param("id") String id);
}
