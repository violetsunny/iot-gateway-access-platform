package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformMeasureRefBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformMeasureRefBoConverter;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformMeasureRefMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrdPlatformMeasureRefRepository extends ServiceImpl<TrdPlatformMeasureRefMapper, TrdPlatformMeasureRefEntity> implements IService<TrdPlatformMeasureRefEntity> {

    @Resource
    TrdPlatformMeasureRefBoConverter trdPlatformMeasureRefBoConverter;

    public List<TrdPlatformMeasureRefBo> queryById(Long modelRefId) {
        LambdaQueryChainWrapper<TrdPlatformMeasureRefEntity> queryChainWrapper = this.lambdaQuery()
                .eq(modelRefId!=null, TrdPlatformMeasureRefEntity::getModelRefId, modelRefId);
        return trdPlatformMeasureRefBoConverter.toTrdPlatformMeasureRefs(queryChainWrapper.list());
    }

}
