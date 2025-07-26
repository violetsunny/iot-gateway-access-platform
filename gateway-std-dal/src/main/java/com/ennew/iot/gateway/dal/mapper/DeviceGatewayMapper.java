package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface DeviceGatewayMapper extends BaseMapper<DeviceGatewayEntity> {

    IPage<DeviceGatewayEntity> queryPage(IPage<DeviceGatewayEntity> page, @Param("params") Map<String, Object> params);

    DeviceGatewayEntity queryById(@Param("id") String id);
}
