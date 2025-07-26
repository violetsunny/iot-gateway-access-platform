package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.CloudDockingDataBO;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudDockingDataEntity;
import com.ennew.iot.gateway.dal.mapper.CloudDockingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:03 2023/7/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudDockingDataRepository extends ServiceImpl<CloudDockingDataMapper, CloudDockingDataEntity> implements IService<CloudDockingDataEntity> {

    private final CloudDockingBoConverter cloudDockingBoConverter;


    public boolean save(CloudDockingDataBO cloudDockingDataBO) {
        CloudDockingDataEntity entity = cloudDockingBoConverter.toCloudDockingDataEntity(cloudDockingDataBO);
        CloudDockingDataEntity entityExist = searchByHostIdUrl(cloudDockingDataBO.getHostId(), cloudDockingDataBO.getRequestUrl());
        if (entityExist == null) {
            return save(entity);
        }
        entity.setId(entityExist.getId());
        return updateById(entity);
    }

    public List<CloudDockingDataBO> searchOneByHostId(String hostId) {
        return cloudDockingBoConverter.toCloudDockingDataBOs(searchByHostId(hostId));
    }

    public List<CloudDockingDataEntity> searchByHostId(String hostId) {
        LambdaQueryChainWrapper<CloudDockingDataEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingDataEntity::getHostId, hostId);
        return queryChainWrapper.list();
    }

    public CloudDockingDataEntity searchByHostIdUrl(String hostId, String url) {
        LambdaQueryChainWrapper<CloudDockingDataEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingDataEntity::getHostId, hostId)
                .eq(StringUtils.hasText(url), CloudDockingDataEntity::getRequestUrl, url);
        return queryChainWrapper.one();
    }
}
