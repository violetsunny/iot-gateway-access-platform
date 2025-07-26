package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NetworkConfigMapper extends BaseMapper<NetworkConfigEntity> {
    List<NetworkConfigEntity> alive(@Param("include") String include, @Param("type") String type);
}
