package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformInfoBoConverter;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TrdPlatformInfoRepository extends ServiceImpl<TrdPlatformInfoMapper, TrdPlatformInfoEntity> implements IService<TrdPlatformInfoEntity> {

    @Resource
    TrdPlatformInfoBoConverter trdPlatformInfoBoConverter;

    public PageResponse<TrdPlatformInfoBo> queryPage(TrdPlatformInfoPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<TrdPlatformInfoEntity> page = baseMapper.queryPage(new PlusPageQuery<TrdPlatformInfoEntity>(pageQuery).getPage(params), params);
        List<TrdPlatformInfoBo> list = trdPlatformInfoBoConverter.toTrdPlatformInfos(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public TrdPlatformInfoBo queryByCode(String code) {
        LambdaQueryChainWrapper<TrdPlatformInfoEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), TrdPlatformInfoEntity::getPCode, code);
        return trdPlatformInfoBoConverter.toTrdPlatformInfo(queryChainWrapper.one());
    }

    public List<TrdPlatformInfoBo> queryByType(Integer ptype) {
        LambdaQueryChainWrapper<TrdPlatformInfoEntity> queryChainWrapper = this.lambdaQuery()
                .eq(ptype!=null, TrdPlatformInfoEntity::getPType, ptype);
        return trdPlatformInfoBoConverter.toTrdPlatformInfos(queryChainWrapper.list());
    }

}
