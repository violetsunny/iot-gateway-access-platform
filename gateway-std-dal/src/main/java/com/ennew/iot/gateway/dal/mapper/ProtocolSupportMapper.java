package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.ProtocolSupportEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ProtocolSupportMapper extends BaseMapper<ProtocolSupportEntity> {
    IPage<ProtocolSupportEntity> queryPage(IPage<ProtocolSupportEntity> page, @Param("params") Map<String, Object> params);
}
