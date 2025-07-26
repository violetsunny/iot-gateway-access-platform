package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.converter.DeviceGatewayBoConverter;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.ennew.iot.gateway.dal.mapper.DeviceGatewayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.exception.BizException;
import top.kdla.framework.infra.dal.mybatis.util.PlusPageQuery;

import java.util.List;
import java.util.Map;

@Service
public class DeviceGatewayRepository extends ServiceImpl<DeviceGatewayMapper, DeviceGatewayEntity> implements IService<DeviceGatewayEntity> {

    @Autowired
    private DeviceGatewayBoConverter deviceGatewayBoConverter;

    public boolean save(DeviceGatewayBo bo) {
        DeviceGatewayEntity entity = deviceGatewayBoConverter.fromDeviceGateway(bo);
        entity.setState(NetworkConfigState.disabled);
        return save(entity);
    }

    public boolean update(DeviceGatewayBo bo) {
        DeviceGatewayEntity entity = deviceGatewayBoConverter.fromDeviceGateway(bo);
        return updateById(entity);
    }

    public DeviceGatewayResBo queryById(String id) {
        DeviceGatewayEntity entity = getBaseMapper().queryById(id);
        if (entity == null) {
            throw new BizException("该设备网关不存在");
        }
        return deviceGatewayBoConverter.toDeviceGatewayRes(entity);
    }

    public boolean delete(String id) {
        DeviceGatewayEntity entity = getById(id);
        if (entity == null) {
            throw new BizException("该设备网关不存在");
        }
        if (NetworkConfigState.enabled.equals(entity.getState())) {
            throw new BizException("该设备网关已启用");
        }
        return removeById(id);
    }

    public PageResponse<DeviceGatewayResBo> queryPage(DeviceGatewayPageQueryBo pageQuery) {
        Map<String, Object> params = BeanUtil.beanToMap(pageQuery);
        IPage<DeviceGatewayEntity> page = baseMapper.queryPage(new PlusPageQuery<DeviceGatewayEntity>(pageQuery).getPage(params), params);
        List<DeviceGatewayResBo> list = deviceGatewayBoConverter.toDeviceGatewayRes(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());
    }

    public boolean updateState(String id, NetworkConfigState state) {
        return lambdaUpdate().set(DeviceGatewayEntity::getState, state).eq(DeviceGatewayEntity::getId, id).update();
    }
}
