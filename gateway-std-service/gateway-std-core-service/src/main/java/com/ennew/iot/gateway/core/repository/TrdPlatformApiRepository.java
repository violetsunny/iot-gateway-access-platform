package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformApiBoConverter;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformApiMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TrdPlatformApiRepository extends ServiceImpl<TrdPlatformApiMapper, TrdPlatformApiEntity> implements IService<TrdPlatformApiEntity> {

    @Resource
    TrdPlatformApiBoConverter trdPlatformApiBoConverter;

    public TrdPlatformApiEntity searchById(Long id) {
        LambdaQueryChainWrapper<TrdPlatformApiEntity> queryChainWrapper = this.lambdaQuery()
                .eq(id!=null, TrdPlatformApiEntity::getId, id);
        return queryChainWrapper.one();
    }

    public TrdPlatformApiBo getById(Long id) {
        TrdPlatformApiEntity entity = searchById(id);
        return trdPlatformApiBoConverter.toTrdPlatformApi(entity);
    }
}
