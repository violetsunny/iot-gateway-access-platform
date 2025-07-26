package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskPageQueryBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformTaskBoConverter;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformTaskEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TrdPlatformTaskRepository extends ServiceImpl<TrdPlatformTaskMapper, TrdPlatformTaskEntity> implements IService<TrdPlatformTaskEntity> {

    @Resource
    TrdPlatformTaskBoConverter trdPlatformTaskBoConverter;

    public PageResponse<TrdPlatformTaskBo> queryPage(TrdPlatformTaskPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<TrdPlatformTaskEntity> page = baseMapper.queryPage(new PlusPageQuery<TrdPlatformTaskEntity>(pageQuery).getPage(params), params);
        List<TrdPlatformTaskBo> list = trdPlatformTaskBoConverter.toTrdPlatformTasks(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public List<TrdPlatformTaskBo> queryByCode(String code) {
        LambdaQueryChainWrapper<TrdPlatformTaskEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), TrdPlatformTaskEntity::getPCode, code);
        return trdPlatformTaskBoConverter.toTrdPlatformTasks(queryChainWrapper.list());
    }

    public TrdPlatformTaskBo searchByCode(String code,String productId,String taskCode) {
        LambdaQueryChainWrapper<TrdPlatformTaskEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), TrdPlatformTaskEntity::getPCode, code)
                .eq(StringUtils.hasText(productId), TrdPlatformTaskEntity::getProductId, productId)
                .eq(StringUtils.hasText(taskCode), TrdPlatformTaskEntity::getTaskCode, taskCode);
        return trdPlatformTaskBoConverter.toTrdPlatformTask(queryChainWrapper.one());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTaskStatus(String code, String productId, String taskCode,Integer status) {
        return lambdaUpdate().set(TrdPlatformTaskEntity::getStatus, status)
                .eq(StringUtils.hasText(code), TrdPlatformTaskEntity::getPCode, code)
                .eq(StringUtils.hasText(productId), TrdPlatformTaskEntity::getProductId, productId)
                .eq(StringUtils.hasText(taskCode), TrdPlatformTaskEntity::getTaskCode, taskCode)
                .update();
    }
}
