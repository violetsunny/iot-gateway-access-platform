package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.ProtocolSupportBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportPageQueryBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportQueryBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportResBo;
import com.ennew.iot.gateway.core.converter.ProtocolSupportBoConverter;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.entity.ProtocolSupportEntity;
import com.ennew.iot.gateway.dal.mapper.ProtocolSupportMapper;
import org.apache.pulsar.shade.org.eclipse.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.exception.BizException;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProtocolSupportRepository extends ServiceImpl<ProtocolSupportMapper, ProtocolSupportEntity> implements IService<ProtocolSupportEntity> {

    @Autowired
    private ProtocolSupportBoConverter protocolSupportBoConverter;

    @Autowired
    private DeviceGatewayRepository deviceGatewayRepository;

    public boolean save(ProtocolSupportBo bo) {
        if (StringUtil.isNotBlank(bo.getId()) && getById(bo.getId()) != null) {
            throw new BizException("Id已存在，换一个试试");
        }
        ProtocolSupportEntity entity = protocolSupportBoConverter.fromProtocolSupport(bo);
        Date cur = new Date();
        entity.setCreateTime(cur);
        entity.setUpdateTime(cur);
        entity.setIsDeleted(0);
        if (bo.getIsTemplate() == null) {
            entity.setIsTemplate((byte) 0);
        }
        return save(entity);
    }

    public boolean update(ProtocolSupportBo bo) {
        ProtocolSupportEntity entity = protocolSupportBoConverter.fromProtocolSupport(bo);
        entity.setUpdateTime(new Date());
        return updateById(entity);
    }

    public ProtocolSupportResBo queryById(String id) {
        ProtocolSupportEntity entity = getById(id);
        if (entity == null) {
            throw new BizException("该协议不存在");
        }
        return protocolSupportBoConverter.toProtocolSupportRes(entity);
    }

    public boolean delete(String id) {
        ProtocolSupportEntity entity = getById(id);
        if (entity == null) {
            throw new BizException("该协议不存在");
        }
        return removeById(id);
    }

    public PageResponse<ProtocolSupportResBo> queryPage(ProtocolSupportPageQueryBo pageQuery) {
        pageQuery.setIsDeleted(0);
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<ProtocolSupportEntity> page = baseMapper.queryPage(new PlusPageQuery<ProtocolSupportEntity>(pageQuery).getPage(params), params);
        List<ProtocolSupportResBo> list = protocolSupportBoConverter.toProtocolSupportRes(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public List<ProtocolSupportResBo> query(ProtocolSupportQueryBo query) {
        LambdaQueryChainWrapper<ProtocolSupportEntity> queryChainWrapper = this.lambdaQuery()
                .eq(StringUtils.hasText(query.getId()), ProtocolSupportEntity::getId, query.getId())
                .like(StringUtils.hasText(query.getName()), ProtocolSupportEntity::getName, query.getName())
                .eq(StringUtils.hasText(query.getType()), ProtocolSupportEntity::getType, query.getType())
                .eq(query.getWay() != null, ProtocolSupportEntity::getWay, query.getWay())
                .eq(query.getState() != null, ProtocolSupportEntity::getState, query.getState())
                .eq(query.getIsTemplate() != null, ProtocolSupportEntity::getIsTemplate, query.getIsTemplate())
                .eq(ProtocolSupportEntity::getIsDeleted, 0);
        if (query.getFilterUnused() != null && query.getFilterUnused()) {
            List<DeviceGatewayEntity> deviceGateways = deviceGatewayRepository.lambdaQuery().select(DeviceGatewayEntity::getProtocol).list();
            if (!CollectionUtils.isEmpty(deviceGateways)) {
                List<String> protocolIds = deviceGateways.stream().map(DeviceGatewayEntity::getProtocol).collect(Collectors.toList());
                queryChainWrapper = queryChainWrapper.notIn(ProtocolSupportEntity::getId, protocolIds);
            }
        }
        List<ProtocolSupportEntity> entities = queryChainWrapper.list();
        if (StringUtils.hasText(query.getInclude())) {
            entities.add(0, getById(query.getInclude()));
        }
        return protocolSupportBoConverter.toProtocolSupportRes(entities);
    }

    public List<ProtocolSupportEntity> queryByName(String name) {
        LambdaQueryWrapper<ProtocolSupportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProtocolSupportEntity::getName, name);
        return baseMapper.selectList(queryWrapper);
    }

}
