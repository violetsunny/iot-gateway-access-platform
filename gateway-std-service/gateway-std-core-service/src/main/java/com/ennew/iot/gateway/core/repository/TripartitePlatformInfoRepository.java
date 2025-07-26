package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.converter.TripartitePlatformInfoBoConverter;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import com.ennew.iot.gateway.dal.mapper.TripartitePlatformInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TripartitePlatformInfoRepository extends ServiceImpl<TripartitePlatformInfoMapper, TripartitePlatformInfoEntity> implements IService<TripartitePlatformInfoEntity> {

    @Resource
    TripartitePlatformInfoBoConverter tripartitePlatformInfoBoConverter;

    public PageResponse<TripartitePlatformInfoBo> queryPage(TripartitePlatformInfoPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<TripartitePlatformInfoEntity> page = baseMapper.queryPage(new PlusPageQuery<TripartitePlatformInfoEntity>(pageQuery).getPage(params), params);
        List<TripartitePlatformInfoBo> list = tripartitePlatformInfoBoConverter.toTripartitePlatformInfos(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public TripartitePlatformInfoBo queryByCode(String code) {
        LambdaQueryChainWrapper<TripartitePlatformInfoEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), TripartitePlatformInfoEntity::getCode, code);
        return tripartitePlatformInfoBoConverter.toTripartitePlatformInfo(queryChainWrapper.one());
    }
}
