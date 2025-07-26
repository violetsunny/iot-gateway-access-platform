package com.ennew.iot.gateway.core.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.NetworkConfigQueryBo;
import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;
import com.ennew.iot.gateway.core.converter.NetworkConfigBoConverter;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import com.ennew.iot.gateway.dal.mapper.NetworkConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.exception.BizException;

import java.util.List;

@Service
public class NetworkConfigRepository extends ServiceImpl<NetworkConfigMapper, NetworkConfigEntity> implements IService<NetworkConfigEntity> {

    @Autowired
    private NetworkConfigBoConverter networkConfigBoConverter;

    public NetworkConfigResBo queryById(String id) {
        NetworkConfigEntity entity = getById(id);
        if (entity == null) {
            throw new BizException("该网络组件不存在");
        }
        return networkConfigBoConverter.toNetworkConfigRes(entity);
    }

    public List<NetworkConfigResBo> query(NetworkConfigQueryBo query) {
        List<NetworkConfigEntity> entities = this.lambdaQuery()
                .eq(StringUtils.hasText(query.getId()), NetworkConfigEntity::getId, query.getId())
                .like(StringUtils.hasText(query.getName()), NetworkConfigEntity::getName, query.getName())
                .eq(StringUtils.hasText(query.getType()), NetworkConfigEntity::getType, query.getType())
                .list();
        return networkConfigBoConverter.toNetworkConfigRes(entities);
    }

    public List<NetworkConfigResBo> alive(String include, String type) {
        List<NetworkConfigEntity> entities = getBaseMapper().alive(include, type);
        return networkConfigBoConverter.toNetworkConfigRes(entities);
    }
}
