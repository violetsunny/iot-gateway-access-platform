/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.processor.listener;

import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.clouddocking.service.HttpCloudService;
import com.ennew.iot.gateway.core.message.CloudWorkDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import top.kdla.framework.log.catchlog.CatchAndLog;

import javax.annotation.Resource;

/**
 * @author kanglele
 * @version $Id: HttpCloudWorkListener, v 0.1 2023/5/23 15:14 kanglele Exp $
 */
@Component
@Slf4j
@CatchAndLog
public class HttpCloudWorkListener {

    @Resource
    private HttpCloudService httpCloudService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id:iot-gateway-other}", topics = {"${spring.kafka.consumer.topics-cloud:iot-gateway-cloud-data}"})
    public void consumer(ConsumerRecord<String, Object> record) {
        try {
            log.info(" 主题：" + record.topic() + "-" + record.partition() + "-" + JSONObject.toJSONString(record.value()));
            //CloudWorkDataMessage message = JSONObject.parseObject(record.value().toString(), CloudWorkDataMessage.class);
            httpCloudService.dealCloudData((CloudWorkDataMessage) record.value());
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

}
