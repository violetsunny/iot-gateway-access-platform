package com.enn.iot.dtu.integration.kafka;

import com.enn.iot.dtu.common.metric.dto.IotMetricMessage;
import com.enn.iot.dtu.common.msg.IotCmdRespond;
import com.enn.iot.dtu.common.outer.event.IotOuterEvent;
import com.enn.iot.dtu.common.outer.msg.dto.KafkaPropertyMessage;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.kafka.properties.IotKafkaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  IotKafkaClient {
    @Autowired
    private IotKafkaProperties iotKafkaProperties;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送数据到 uncim 主题
     *
     * @param message
     */
    public void sendUncimData(KafkaPropertyMessage message) {
        kafkaTemplate.send(iotKafkaProperties.getTopicUncimData(), message);
    }

    /**
     * 发送数据到监控指标主题
     *
     * @param messages
     */
    public void sendMetricData(List<IotMetricMessage> messages) {
        kafkaTemplate.send(iotKafkaProperties.getTopicMetricData(), messages);
    }

    public void sendEventData(IotOuterEvent event) {
        kafkaTemplate.send(iotKafkaProperties.getTopicEventData(), event);
    }


    /**
     * 发送数据到指令回执接口
     *
     * @param message
     */
    public void sendCmdRespond(IotCmdRespond message) {
        kafkaTemplate.send(iotKafkaProperties.getTopicCmdRespond(), message);
    }


}

