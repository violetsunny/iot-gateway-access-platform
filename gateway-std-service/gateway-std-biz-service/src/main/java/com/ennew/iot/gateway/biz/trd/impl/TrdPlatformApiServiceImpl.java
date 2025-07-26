package com.ennew.iot.gateway.biz.trd.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.trd.TrdPlatformApiService;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiQueryBo;
import com.ennew.iot.gateway.core.repository.TrdPlatformApiParamRepository;
import com.ennew.iot.gateway.core.repository.TrdPlatformApiRepository;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformApiMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.dto.MultiResponse;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrdPlatformApiServiceImpl extends ServiceImpl<TrdPlatformApiMapper, TrdPlatformApiEntity> implements TrdPlatformApiService {

    @Autowired
    private TrdPlatformApiRepository trdPlatformApiRepository;

    @Autowired
    private TrdPlatformApiParamRepository trdPlatformApiParamRepository;

    @Override
    public MultiResponse<TrdPlatformApiBo> list(TrdPlatformApiQueryBo trdPlatformApiQueryBo) {
        return null;
    }

    @Override
    public TrdPlatformApiBo getDetailById(String id) {
        TrdPlatformApiBo trdPlatformApiBo = new TrdPlatformApiBo();
        TrdPlatformApiEntity trdPlatformApi = trdPlatformApiRepository.getById(id);
        BeanUtil.copyProperties(trdPlatformApi, trdPlatformApiBo);
        if (trdPlatformApi != null) {
            LambdaQueryWrapper<TrdPlatformApiParamEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TrdPlatformApiParamEntity::getApiId, trdPlatformApi.getId());
            List<TrdPlatformApiParamEntity> param = trdPlatformApiParamRepository.list(queryWrapper);
            trdPlatformApiBo.setApiParams(param);
        }
        return trdPlatformApiBo;
    }

    @Override
    public Boolean saveApi(TrdPlatformApiBo trdPlatformApiBo) {
        TrdPlatformApiEntity entity = new TrdPlatformApiEntity();
        BeanUtil.copyProperties(trdPlatformApiBo, entity);
        Date current = new Date();
        entity.setCreateTime(current);
        entity.setUpdateTime(current);
        entity.setStatus(1);
        entity.setIsDelete(0);
        trdPlatformApiRepository.save(entity);
        if (!CollectionUtils.isEmpty(trdPlatformApiBo.getApiParams())) {
            trdPlatformApiBo.getApiParams().forEach(p -> initTrdPlatformApiParam(p, entity.getId(), current));
            trdPlatformApiParamRepository.saveBatch(trdPlatformApiBo.getApiParams());
        }
        return true;
    }

    @Override
    public Boolean updateApiById(TrdPlatformApiBo trdPlatformApiBo) {
        TrdPlatformApiEntity entity = new TrdPlatformApiEntity();
        BeanUtil.copyProperties(trdPlatformApiBo, entity);
        Date current = new Date();
        entity.setUpdateTime(current);
        trdPlatformApiRepository.updateById(entity);
        trdPlatformApiParamRepository.getBaseMapper().deleteByApiId(entity.getId());
        if (!CollectionUtils.isEmpty(trdPlatformApiBo.getApiParams())) {
            trdPlatformApiBo.getApiParams().forEach(p -> initTrdPlatformApiParam(p, entity.getId(), current));
            trdPlatformApiParamRepository.saveBatch(trdPlatformApiBo.getApiParams());
        }
        return true;
    }

    public void initTrdPlatformApiParam(TrdPlatformApiParamEntity param, Long id, Date current) {
        param.setApiId(id);
        param.setCreateTime(current);
        param.setUpdateTime(current);
        param.setStatus(1);
        param.setIsDelete(0);
    }

    @Override
    public boolean isExistName(String code,String name) {
        LambdaQueryWrapper<TrdPlatformApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformApiEntity::getPCode,code)
        .eq(TrdPlatformApiEntity::getApiName, name)
        .eq(TrdPlatformApiEntity::getIsDelete,0);
        List<TrdPlatformApiEntity> list = trdPlatformApiRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public boolean isExistCode(String code) {
        LambdaQueryWrapper<TrdPlatformApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrdPlatformApiEntity::getPCode, code)
                .eq(TrdPlatformApiEntity::getIsDelete,0);
        List<TrdPlatformApiEntity> list = trdPlatformApiRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public MultiResponse<TrdPlatformApiBo> listApi(TrdPlatformApiQueryBo trdPlatformApiQueryBo) {
        LambdaQueryWrapper<TrdPlatformApiEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(trdPlatformApiQueryBo.getApiName())) {
            queryWrapper.eq(TrdPlatformApiEntity::getApiName, trdPlatformApiQueryBo.getApiName());
        }
        if (StringUtils.isNotEmpty(trdPlatformApiQueryBo.getPCode())) {
            queryWrapper.eq(TrdPlatformApiEntity::getPCode, trdPlatformApiQueryBo.getPCode());
        }
        if (trdPlatformApiQueryBo.getApiType() != null) {
            queryWrapper.eq(TrdPlatformApiEntity::getApiType, trdPlatformApiQueryBo.getApiType());
        }
        List<TrdPlatformApiEntity> list = trdPlatformApiRepository.list(queryWrapper);
        return MultiResponse.buildSuccess(list.stream().map(e -> BeanUtil.copyProperties(e, TrdPlatformApiBo.class)).collect(Collectors.toList()));
    }

    @Override
    public Boolean removeApiById(Long id) {
        trdPlatformApiRepository.removeById(id);
        trdPlatformApiParamRepository.getBaseMapper().deleteByApiId(id);
        return true;
    }

}
