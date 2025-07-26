package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudDockingEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.ennew.iot.gateway.dal.mapper.CloudDockingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午1:30 2023/5/23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudDockingRepository extends ServiceImpl<CloudDockingMapper, CloudDockingEntity> implements IService<CloudDockingEntity> {

    private final CloudDockingBoConverter cloudDockingBoConverter;

    public Boolean saveCloudDocking(CloudDockingBO cloudDockingBO) {
        CloudDockingEntity cloudDockingEntity =  cloudDockingBoConverter.toCloudDockingEntity(cloudDockingBO);
        if (searchByCode(cloudDockingEntity.getCode()) != null) {
            log.error("编码已存在{}",cloudDockingEntity.getCode());
            throw new BizException(ErrorCode.BIZ_ERROR);
        }
        cloudDockingEntity.setState(NetworkConfigState.paused.getName());
        cloudDockingEntity.setCreateTime(new Date());
        return save(cloudDockingEntity);
    }

    public PageResponse<CloudDockingResBO> queryPage(CloudDockingPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<CloudDockingEntity> page = baseMapper.queryPage(new PlusPageQuery<CloudDockingEntity>(pageQuery).getPage(params), params);
        List<CloudDockingResBO> list = cloudDockingBoConverter.toCloudDockingRes(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }


    public boolean updateState(String id, NetworkConfigState state) {
        return lambdaUpdate().set(CloudDockingEntity::getState, state.getName()).eq(CloudDockingEntity::getId, id).update();
    }

    public CloudDockingEntity searchByCode(String code) {
        LambdaQueryChainWrapper<CloudDockingEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(code), CloudDockingEntity::getCode, code);
        return queryChainWrapper.one();
    }

    public CloudDockingResBO getByCode(String code) {
        CloudDockingEntity entity = searchByCode(code);
        return cloudDockingBoConverter.toCloudDockingRes(entity);
    }

    public CloudDockingResBO getCloudDockingBO(String id) {
        CloudDockingEntity entity = getById(id);
        return cloudDockingBoConverter.toCloudDockingRes(entity);
    }

}
