package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.CloudDockingAuthBO;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudDockingAuthEntity;
import com.ennew.iot.gateway.dal.mapper.CloudDockingAuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import java.util.Objects;

/**
 * @Author: alec
 * Description:
 * @date: 下午3:44 2023/7/12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudDockingAuthRepository extends ServiceImpl<CloudDockingAuthMapper, CloudDockingAuthEntity> implements IService<CloudDockingAuthEntity> {

    private final CloudDockingBoConverter cloudDockingBoConverter;


    public boolean save(CloudDockingAuthBO cloudDockingAuthBO) {
        CloudDockingAuthEntity entity = cloudDockingBoConverter.toCloudDockingAuthEntity(cloudDockingAuthBO);
        CloudDockingAuthEntity entityExist = searchByHostId(cloudDockingAuthBO.getHostId());
        if (entityExist == null) {
            return save(entity);
        }
        entity.setId(entityExist.getId());

        return updateById(entity);
    }

    public CloudDockingAuthEntity searchByHostId(String hostId) {
        LambdaQueryChainWrapper<CloudDockingAuthEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingAuthEntity::getHostId, hostId);
        return queryChainWrapper.one();
    }


    public CloudDockingAuthBO searchOneByHostId(String hostId) {
        LambdaQueryChainWrapper<CloudDockingAuthEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingAuthEntity::getHostId, hostId);
        CloudDockingAuthEntity entity = queryChainWrapper.one();
        return cloudDockingBoConverter.toCloudDockingRespBo(entity);
    }

    public void removeByHost(String hostId) {
        LambdaQueryChainWrapper<CloudDockingAuthEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingAuthEntity::getHostId, hostId);
        CloudDockingAuthEntity entity = queryChainWrapper.one();
        if (Objects.isNull(entity)) {
            return;
        }
        removeById(entity.getId());
    }

}
