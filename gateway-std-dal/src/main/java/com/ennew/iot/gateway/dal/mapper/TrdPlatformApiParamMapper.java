package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;

/**
 * @author ruanhong
 * @description trd_platform_api_param
 * @date 2024-03-13
 */
public interface TrdPlatformApiParamMapper extends BaseMapper<TrdPlatformApiParamEntity> {

    int deleteByApiId(Long apiId);

}