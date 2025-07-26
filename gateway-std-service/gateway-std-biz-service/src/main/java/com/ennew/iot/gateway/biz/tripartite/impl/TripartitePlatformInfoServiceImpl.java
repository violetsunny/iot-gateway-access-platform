package com.ennew.iot.gateway.biz.tripartite.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.tripartite.TripartitePlatformInfoService;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoQueryBo;
import com.ennew.iot.gateway.core.repository.TripartitePlatformInfoRepository;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import com.ennew.iot.gateway.dal.mapper.TripartitePlatformInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.List;

@Service
public class TripartitePlatformInfoServiceImpl extends ServiceImpl<TripartitePlatformInfoMapper, TripartitePlatformInfoEntity> implements TripartitePlatformInfoService {

    @Autowired
    private TripartitePlatformInfoRepository tripartitePlatformInfoRepository;

    @Override
    public PageResponse<TripartitePlatformInfoBo> queryPage(TripartitePlatformInfoPageQueryBo tripartitePlatformInfoPageQueryBo) {
        return tripartitePlatformInfoRepository.queryPage(tripartitePlatformInfoPageQueryBo);
    }

    @Override
    public boolean isExistName(String name) {
        LambdaQueryWrapper<TripartitePlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TripartitePlatformInfoEntity::getName, name);
        List<TripartitePlatformInfoEntity> list = tripartitePlatformInfoRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public boolean isExistCode(String code) {
        LambdaQueryWrapper<TripartitePlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TripartitePlatformInfoEntity::getCode, code);
        List<TripartitePlatformInfoEntity> list = tripartitePlatformInfoRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public MultiResponse<TripartitePlatformInfoBo> list(TripartitePlatformInfoQueryBo tripartitePlatformInfoQueryBo) {
        LambdaQueryWrapper<TripartitePlatformInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(tripartitePlatformInfoQueryBo.getName())) {
            queryWrapper.like(TripartitePlatformInfoEntity::getName, tripartitePlatformInfoQueryBo.getName());
        }
        if (!StringUtils.isEmpty(tripartitePlatformInfoQueryBo.getCode())) {
            queryWrapper.like(TripartitePlatformInfoEntity::getCode, tripartitePlatformInfoQueryBo.getCode());
        }
        List<TripartitePlatformInfoEntity> list = tripartitePlatformInfoRepository.list(queryWrapper);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list, TripartitePlatformInfoBo.class));
    }

}
