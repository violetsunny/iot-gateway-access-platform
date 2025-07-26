package com.ennew.iot.gateway.biz.trd.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.trd.TrdPlatformModelRefService;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefQueryBo;
import com.ennew.iot.gateway.core.repository.TrdPlatformModelRefRepository;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformModelRefMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.List;

@Service
public class TrdPlatformModelRefServiceImpl extends ServiceImpl<TrdPlatformModelRefMapper, TrdPlatformModelRefEntity> implements TrdPlatformModelRefService {

    @Autowired
    private TrdPlatformModelRefRepository trdPlatformModelRefRepository;

    @Override
    public PageResponse<TrdPlatformModelRefBo> queryPage(TrdPlatformModelRefPageQueryBo tdPlatformModelRefPageQueryBo) {
        return trdPlatformModelRefRepository.queryPage(tdPlatformModelRefPageQueryBo);
    }

    @Override
    public String entityParamCheck(TrdPlatformModelRefEntity trdPlatformModelRefEntity) {
        LambdaQueryWrapper<TrdPlatformModelRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformModelRefEntity::getPlatformCode, trdPlatformModelRefEntity.getPlatformCode());
        queryWrapper.eq(TrdPlatformModelRefEntity::getIsDelete,0);
        if (trdPlatformModelRefEntity.getId() != null) {
            queryWrapper.ne(TrdPlatformModelRefEntity::getId, trdPlatformModelRefEntity.getId());
        }
        List<TrdPlatformModelRefEntity> platformModelRefList = trdPlatformModelRefRepository.list(queryWrapper);
        for (TrdPlatformModelRefEntity m : platformModelRefList) {
            if (m.getPlatformModelCode().equals(trdPlatformModelRefEntity.getPlatformModelCode())) {
                if (m.getEnnProductId().equals(trdPlatformModelRefEntity.getEnnProductId())) {
                    return "当前映射关系已存在，请勿重复映射";
                }
                if (!m.getPlatformModelName().equals(trdPlatformModelRefEntity.getPlatformModelName())) {
                    return "当前三方模型名称与同一个三方模型编码的名称不同，请保持名称一致!";
                }
            }
            if (m.getPlatformModelName().equals(trdPlatformModelRefEntity.getPlatformModelName())) {
                if (m.getEnnProductId().equals(trdPlatformModelRefEntity.getEnnProductId())) {
                    return "当前映射关系已存在，请勿重复映射";
                }
                if (!m.getPlatformModelCode().equals(trdPlatformModelRefEntity.getPlatformModelCode())) {
                    return "当前三方模型编码与同一个三方模型名称的编码不同，请保持编码一致!";
                }
            }
        }
        return null;
    }

    @Override
    public MultiResponse<TrdPlatformModelRefBo> list(TrdPlatformModelRefQueryBo trdPlatformModelRefRQueryBo) {
        LambdaQueryWrapper<TrdPlatformModelRefEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(trdPlatformModelRefRQueryBo.getPlatformCode())) {
            queryWrapper.eq(TrdPlatformModelRefEntity::getPlatformCode, trdPlatformModelRefRQueryBo.getPlatformCode());
        }
        if (!StringUtils.isEmpty(trdPlatformModelRefRQueryBo.getPlatformModelName())) {
            queryWrapper.like(TrdPlatformModelRefEntity::getPlatformModelName, trdPlatformModelRefRQueryBo.getPlatformModelName());
        }
        if (!StringUtils.isEmpty(trdPlatformModelRefRQueryBo.getPlatformModelCode())) {
            queryWrapper.eq(TrdPlatformModelRefEntity::getPlatformModelCode, trdPlatformModelRefRQueryBo.getPlatformModelCode());
        }
        queryWrapper.eq(TrdPlatformModelRefEntity::getIsDelete,0);
        List<TrdPlatformModelRefEntity> list = trdPlatformModelRefRepository.list(queryWrapper);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list, TrdPlatformModelRefBo.class));
    }

}
