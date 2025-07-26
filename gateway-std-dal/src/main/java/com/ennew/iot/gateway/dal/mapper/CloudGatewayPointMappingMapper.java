package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CloudGatewayPointMappingMapper extends BaseMapper<CloudGatewayPointMappingEntity> {



    Page<Map<String, Object>> queryMappingPage(Page<Map<String, Object>> page,
                                                     @Param("gatewayCode") String gatewayCode,
                                                     @Param("type") String type,
                                                     @Param("deviceId") String deviceId);



}
