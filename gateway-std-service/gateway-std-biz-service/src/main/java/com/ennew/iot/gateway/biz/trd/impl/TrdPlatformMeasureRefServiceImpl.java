package com.ennew.iot.gateway.biz.trd.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.trd.TrdPlatformMeasureRefService;
import com.ennew.iot.gateway.core.repository.TrdPlatformMeasureRefRepository;
import com.ennew.iot.gateway.core.repository.TrdPlatformModelRefRepository;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformMeasureRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrdPlatformMeasureRefServiceImpl extends ServiceImpl<TrdPlatformMeasureRefMapper, TrdPlatformMeasureRefEntity> implements TrdPlatformMeasureRefService {

    @Autowired
    private TrdPlatformMeasureRefRepository trdPlatformMeasureRefRepository;

    @Autowired
    private TrdPlatformModelRefRepository trdPlatformModelRefRepository;

    @Override
    public String entityParamCheck(TrdPlatformMeasureRefEntity trdPlatformMeasureRefEntity) {
        LambdaQueryWrapper<TrdPlatformMeasureRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformMeasureRefEntity::getModelRefId, trdPlatformMeasureRefEntity.getModelRefId());
        queryWrapper.eq(TrdPlatformMeasureRefEntity::getIsDelete,0);
        if (trdPlatformMeasureRefEntity.getId() != null) {
            queryWrapper.ne(TrdPlatformMeasureRefEntity::getId, trdPlatformMeasureRefEntity.getId());
        }
        List<TrdPlatformMeasureRefEntity> platformMeasureRefList = trdPlatformMeasureRefRepository.list(queryWrapper);
        for (TrdPlatformMeasureRefEntity m : platformMeasureRefList) {
            if (m.getEnnMeasureCode().equals(trdPlatformMeasureRefEntity.getEnnMeasureCode())) {
                return "当前物模型量测属性已被映射";
            }
            if (m.getPlatformMeasureCode().equals(trdPlatformMeasureRefEntity.getPlatformMeasureCode())) {
                if (!m.getPlatformMeasureName().equals(trdPlatformMeasureRefEntity.getPlatformMeasureName())) {
                    return "当前三方平台测点名称与同一个三方测点编码的名称不同，请保持名称一致!";
                }
            }
            if (m.getPlatformMeasureName().equals(trdPlatformMeasureRefEntity.getPlatformMeasureName())) {
                if (!m.getPlatformMeasureCode().equals(trdPlatformMeasureRefEntity.getPlatformMeasureCode())) {
                    return "当前三方平台测点编码与同一个三方测点名称的编码不同，请保持编码一致!";
                }
            }
        }
        return null;
    }

}
