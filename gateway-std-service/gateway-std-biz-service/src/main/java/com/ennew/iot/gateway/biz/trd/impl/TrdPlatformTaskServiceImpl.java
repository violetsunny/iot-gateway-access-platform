package com.ennew.iot.gateway.biz.trd.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ennew.iot.gateway.biz.trd.TrdPlatformTaskService;
import com.ennew.iot.gateway.common.enums.OperateEnum;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskQueryBo;
import com.ennew.iot.gateway.core.converter.TrdPlatformTaskBoConverter;
import com.ennew.iot.gateway.core.message.TrdPlatformTaskMessage;
import com.ennew.iot.gateway.core.repository.TrdPlatformTaskRepository;
import com.ennew.iot.gateway.core.service.KafkaProducer;
import com.ennew.iot.gateway.dal.entity.TrdPlatformTaskEntity;
import com.ennew.iot.gateway.dal.mapper.TrdPlatformTaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrdPlatformTaskServiceImpl extends ServiceImpl<TrdPlatformTaskMapper, TrdPlatformTaskEntity> implements TrdPlatformTaskService {

    @Autowired
    private TrdPlatformTaskRepository trdPlatformTaskRepository;
    @Resource
    private KafkaProducer kafkaProducer;
    @Resource
    TrdPlatformTaskBoConverter trdPlatformTaskBoConverter;

    @Value("${spring.kafka.consumer.topics-trdPlatform-task:iot_gateway_trdPlatform_data}")
    private String cloudTopic;

    @Override
    public PageResponse<TrdPlatformTaskBo> queryPage(TrdPlatformTaskPageQueryBo tdPlatformTaskPageQueryBo) {
        return trdPlatformTaskRepository.queryPage(tdPlatformTaskPageQueryBo);
    }

    @Override
    public boolean isExistName(String platformCode, String name) {
        LambdaQueryWrapper<TrdPlatformTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TrdPlatformTaskEntity::getPCode, platformCode)
                .eq(TrdPlatformTaskEntity::getTaskName, name)
                .eq(TrdPlatformTaskEntity::getIsDelete, 0);
        List<TrdPlatformTaskEntity> list = trdPlatformTaskRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public boolean isExistCode(String platformCode, String code) {
        LambdaQueryWrapper<TrdPlatformTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TrdPlatformTaskEntity::getPCode, platformCode)
                .eq(TrdPlatformTaskEntity::getTaskCode, code)
                .eq(TrdPlatformTaskEntity::getIsDelete, 0);
        List<TrdPlatformTaskEntity> list = trdPlatformTaskRepository.list(queryWrapper);
        return !CollectionUtils.isEmpty(list);
    }

    @Override
    public MultiResponse<TrdPlatformTaskBo> list(TrdPlatformTaskQueryBo trdPlatformTaskQueryBo) {
        LambdaQueryWrapper<TrdPlatformTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(trdPlatformTaskQueryBo.getTaskName())) {
            queryWrapper.like(TrdPlatformTaskEntity::getTaskName, trdPlatformTaskQueryBo.getTaskName());
        }
        if (!StringUtils.isEmpty(trdPlatformTaskQueryBo.getPCode())) {
            queryWrapper.eq(TrdPlatformTaskEntity::getPCode, trdPlatformTaskQueryBo.getPCode());
        }
        if (!StringUtils.isEmpty(trdPlatformTaskQueryBo.getTaskCode())) {
            queryWrapper.eq(TrdPlatformTaskEntity::getTaskCode, trdPlatformTaskQueryBo.getTaskCode());
        }
        if (!StringUtils.isEmpty(trdPlatformTaskQueryBo.getProductId())) {
            queryWrapper.eq(TrdPlatformTaskEntity::getProductId, trdPlatformTaskQueryBo.getProductId());
        }
        List<TrdPlatformTaskEntity> list = trdPlatformTaskRepository.list(queryWrapper);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(list, TrdPlatformTaskBo.class));
    }

    @Override
    public Boolean saveTask(TrdPlatformTaskEntity entity) {
        Boolean res = this.save(entity);
        if (res) {
            TrdPlatformTaskMessage message = trdPlatformTaskBoConverter.fromTrdPlatformTaskEntity(entity);
            message.setOperate(OperateEnum.ADD.getCode());
            kafkaProducer.send(cloudTopic, JSON.toJSONString(message));
        }
        return res;
    }

    @Override
    public Boolean updateTask(TrdPlatformTaskEntity updateEntity) {
        updateEntity.setPCode(null);
        updateEntity.setTaskCode(null);
        Boolean res = this.updateById(updateEntity);
        if (res) {
            TrdPlatformTaskEntity entity = this.getById(updateEntity.getId());
            TrdPlatformTaskMessage message = trdPlatformTaskBoConverter.fromTrdPlatformTaskEntity(entity);
            message.setOperate(OperateEnum.UPDATE.getCode());
            kafkaProducer.send(cloudTopic, JSON.toJSONString(message));
        }
        return res;
    }

    @Override
    public Boolean removeTask(Long id) {
        TrdPlatformTaskEntity entity = this.getById(id);
        if (entity == null) {
            return false;
        } else {
            Boolean res = this.removeById(id);
            if (res) {
                TrdPlatformTaskMessage message = trdPlatformTaskBoConverter.fromTrdPlatformTaskEntity(entity);
                message.setOperate(OperateEnum.REMOVE.getCode());
                kafkaProducer.send(cloudTopic, JSON.toJSONString(message));
            }
            return res;
        }
    }
}
