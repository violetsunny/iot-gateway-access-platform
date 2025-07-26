package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface TripartitePlatformInfoMapper extends BaseMapper<TripartitePlatformInfoEntity> {

    IPage<TripartitePlatformInfoEntity> queryPage(IPage<TripartitePlatformInfoEntity> page, @Param("params") Map<String, Object> params);

}
