package com.ennew.iot.gateway.biz.trd.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.trd.TrdPlatformInfoService;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoQueryBo;
import com.ennew.iot.gateway.core.repository.TrdPlatformInfoRepository;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrdPlatformInfoServiceImpl extends ServiceImpl<TrdPlatformInfoMapper, TrdPlatformInfoEntity> implements TrdPlatformInfoService {

    @Autowired
    private TrdPlatformInfoRepository trdPlatformInfoRepository;

    @Override
    public PageResponse<TrdPlatformInfoBo> queryPage(TrdPlatformInfoPageQueryBo tdPlatformInfoPageQueryBo) {
        return trdPlatformInfoRepository.queryPage(tdPlatformInfoPageQueryBo);
    }

    @Override
    public boolean isExistName(String name) {
        LambdaQueryWrapper<TrdPlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformInfoEntity::getPName, name)
                .eq(TrdPlatformInfoEntity::getIsDelete,0);
        List<TrdPlatformInfoEntity> list = trdPlatformInfoRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public TrdPlatformInfoEntity getByPCode(String pCode) {
        LambdaQueryWrapper<TrdPlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformInfoEntity::getPCode, pCode)
                .eq(TrdPlatformInfoEntity::getIsDelete,0);
        return trdPlatformInfoRepository.getOne(queryWrapper, false);
    }

    @Override
    public Map<String, TrdPlatformInfoEntity> getByPCodes(List<String> pCodes) {
        LambdaQueryWrapper<TrdPlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TrdPlatformInfoEntity::getPCode, pCodes)
                .eq(TrdPlatformInfoEntity::getIsDelete,0);
        List<TrdPlatformInfoEntity> list = trdPlatformInfoRepository.list(queryWrapper);
        return list.stream().collect(Collectors.toMap(TrdPlatformInfoEntity::getPCode, Function.identity()));
    }

    @Override
    public boolean isExistCode(String code) {
        LambdaQueryWrapper<TrdPlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformInfoEntity::getPCode, code)
                .eq(TrdPlatformInfoEntity::getIsDelete,0);
        List<TrdPlatformInfoEntity> list = trdPlatformInfoRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public MultiResponse<TrdPlatformInfoBo> list(TrdPlatformInfoQueryBo trdPlatformInfoQueryBo) {
        LambdaQueryWrapper<TrdPlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(trdPlatformInfoQueryBo.getPName())) {
            queryWrapper.like(TrdPlatformInfoEntity::getPName, trdPlatformInfoQueryBo.getPName());
        }
        if (!StringUtils.isEmpty(trdPlatformInfoQueryBo.getPCode())) {
            queryWrapper.eq(TrdPlatformInfoEntity::getPCode, trdPlatformInfoQueryBo.getPCode());
        }
        if (trdPlatformInfoQueryBo.getPType() != null) {
            queryWrapper.eq(TrdPlatformInfoEntity::getPType, trdPlatformInfoQueryBo.getPType());
        }
        if (StringUtils.isNotBlank(trdPlatformInfoQueryBo.getPSource())) {
            queryWrapper.eq(TrdPlatformInfoEntity::getPSource, trdPlatformInfoQueryBo.getPSource());
        }
        queryWrapper.eq(TrdPlatformInfoEntity::getIsDelete,0);
        List<TrdPlatformInfoEntity> list = trdPlatformInfoRepository.list(queryWrapper);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list, TrdPlatformInfoBo.class));
    }

    @Override
    public boolean updateConfig(Long id, String configJson) {
        return trdPlatformInfoRepository.update(Wrappers.<TrdPlatformInfoEntity>lambdaUpdate()
                .eq(TrdPlatformInfoEntity::getId, id)
                .set(TrdPlatformInfoEntity::getConfigJson, configJson)
        );
    }

}
