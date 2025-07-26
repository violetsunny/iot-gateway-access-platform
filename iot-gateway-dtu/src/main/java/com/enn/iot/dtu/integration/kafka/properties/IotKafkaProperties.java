package com.enn.iot.dtu.integration.kafka.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "enn.kafka")
@Slf4j
@Data
public class IotKafkaProperties {

    /**
     * uncim 数据主题
     */
//    String topicUncimData = "data_iot_std_uncim";
    String topicUncimData = "enn_data_iot_metric";

    /**
     * 监控指标 Kafka 主题
     */
    String topicMetricData = "data_iot_IOTM";

    /**
     * 事件消息 Kafka 主题
     */
    String topicEventData = "data_iot_dtu_event";


    /**
     * 指令回执主题
     */
    String topicCmdRespond = "data_iot_cmdrespond";
}
