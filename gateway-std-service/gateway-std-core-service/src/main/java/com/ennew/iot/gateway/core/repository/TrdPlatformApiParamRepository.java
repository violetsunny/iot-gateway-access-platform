package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiParamBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformApiParamBoConverter;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformApiParamMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrdPlatformApiParamRepository extends ServiceImpl<TrdPlatformApiParamMapper, TrdPlatformApiParamEntity> implements IService<TrdPlatformApiParamEntity> {

    @Resource
    private TrdPlatformApiParamBoConverter trdPlatformApiParamBoConverter;

    public List<TrdPlatformApiParamEntity> searchById(Long apiId) {
        LambdaQueryChainWrapper<TrdPlatformApiParamEntity> queryChainWrapper = this.lambdaQuery()
                .eq(apiId!=null, TrdPlatformApiParamEntity::getApiId, apiId);
        return queryChainWrapper.list();
    }

    public List<TrdPlatformApiParamBo> getById(Long apiId) {
        List<TrdPlatformApiParamEntity> entity = searchById(apiId);
        return trdPlatformApiParamBoConverter.toTrdPlatformApiParams(entity);
    }

}
