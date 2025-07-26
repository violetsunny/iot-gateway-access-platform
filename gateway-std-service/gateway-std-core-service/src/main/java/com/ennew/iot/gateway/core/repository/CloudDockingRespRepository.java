package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.CloudDockingAuthBO;
import com.ennew.iot.gateway.core.bo.CloudDockingAuthResBO;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudDockingAuthEntity;
import com.ennew.iot.gateway.dal.entity.CloudDockingRespEntity;
import com.ennew.iot.gateway.dal.mapper.CloudDockingRespMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.exception.BizException;

/**
 * @Author: alec
 * Description:
 * @date: 下午5:10 2023/5/29
 */
@Service
@RequiredArgsConstructor
public class CloudDockingRespRepository extends ServiceImpl<CloudDockingRespMapper, CloudDockingRespEntity> implements IService<CloudDockingRespEntity> {

    private final CloudDockingBoConverter cloudDockingBoConverter;

    public CloudDockingAuthResBO getCloudDockingAuthResBO(String hostId) {
        return cloudDockingBoConverter.toCloudDockingRespBo(searchByHostId(hostId));
    }

    public CloudDockingRespEntity searchByHostId(String hostId) {
        LambdaQueryChainWrapper<CloudDockingRespEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingRespEntity::getHostId,  hostId);
        return queryChainWrapper.one();
    }

    public boolean saveRes(CloudDockingAuthResBO cloudDockingAuthBO) {
        CloudDockingRespEntity entity = cloudDockingBoConverter.toCloudDockingRespEntity(cloudDockingAuthBO);
        CloudDockingRespEntity entityExist = searchByHostId(cloudDockingAuthBO.getHostId());
        if (entityExist == null) {
            save(entity);
        }
        //更新
        entity.setId(entityExist.getId());
        return updateById(entity);
    }
}
