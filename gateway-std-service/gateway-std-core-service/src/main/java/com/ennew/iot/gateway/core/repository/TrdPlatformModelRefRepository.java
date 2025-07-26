package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefPageQueryBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformModelRefBoConverter;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformModelRefMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TrdPlatformModelRefRepository extends ServiceImpl<TrdPlatformModelRefMapper, TrdPlatformModelRefEntity> implements IService<TrdPlatformModelRefEntity> {

    @Resource
    TrdPlatformModelRefBoConverter trdPlatformModelRefBoConverter;

    public PageResponse<TrdPlatformModelRefBo> queryPage(TrdPlatformModelRefPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<TrdPlatformModelRefEntity> page = baseMapper.queryPage(new PlusPageQuery<TrdPlatformModelRefEntity>(pageQuery).getPage(params), params);
        List<TrdPlatformModelRefBo> list = trdPlatformModelRefBoConverter.toTrdPlatformModelRefs(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public TrdPlatformModelRefBo queryByCode(String code,String productId) {
        LambdaQueryChainWrapper<TrdPlatformModelRefEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), TrdPlatformModelRefEntity::getPlatformCode, code)
                .eq(StringUtils.hasText(productId), TrdPlatformModelRefEntity::getEnnProductId, productId);
        return trdPlatformModelRefBoConverter.toTrdPlatformModelRef(queryChainWrapper.one());
    }

}
