package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.dal.entity.CloudDockingMetadataEntity;
import com.ennew.iot.gateway.dal.mapper.CloudDockingMetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:33 2023/5/30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudDockingMetadataRepository extends ServiceImpl<CloudDockingMetadataMapper, CloudDockingMetadataEntity>
        implements IService<CloudDockingMetadataEntity> {

    private final CloudDockingBoConverter cloudDockingBoConverter;

    public List<MetadataMapping> getMetadataMapping(String hostId, String dataCode) {

        LambdaQueryChainWrapper<CloudDockingMetadataEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(hostId), CloudDockingMetadataEntity::getHostId, hostId)
                .eq(StringUtils.hasText(dataCode), CloudDockingMetadataEntity::getDataCode, dataCode);
        List<CloudDockingMetadataEntity> authParamsEntityList = queryChainWrapper.list();
        return cloudDockingBoConverter.toCloudDockingMetadata(authParamsEntityList);
    }
}
