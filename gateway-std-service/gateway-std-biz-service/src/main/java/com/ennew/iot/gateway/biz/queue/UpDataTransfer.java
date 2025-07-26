package com.ennew.iot.gateway.biz.queue;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.biz.concurrent.ThreadFactoryImpl;
import com.ennew.iot.gateway.biz.prometheus.MetricCollector;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.protocol.model.EventRequest;
import com.ennew.iot.gateway.client.protocol.model.InfoReportRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import com.ennew.iot.gateway.common.constants.RedisConstant;
import com.ennew.iot.gateway.core.message.KafkaEventMessage;
import com.ennew.iot.gateway.core.message.KafkaNewMessage;
import com.ennew.iot.gateway.core.service.KafkaProducer;
import com.ennew.iot.gateway.core.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 上行数据中转
 *
 * @author hanyilong@enn.cn
 * @since 2021-02-08 16:07:47
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class UpDataTransfer {

//    /**
//     * 线程池
//     */
//    private int poolSize;


//    /**
//     * 上行消息缓冲
//     */
//    private BlockingQueue<Message> up2MQQueue;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${ennew.iot.device.topic:enn_data_iot_metric}")
    private String deviceTopic;
    @Value("${ennew.iot.event.topic:data_iot_event}")
    private String eventTopic;
    @Value("${ennew.iot.device.infoTopic:data_iot_device_info}")
    private String deviceInfoTopic;
    @Value("${ennew.switch.filterMetric:false}")
    private boolean filterUnmatchMetricSwitch;

//    public UpDataTransfer(BlockingQueue<Message> queue, int poolSize) {
//        this.poolSize = poolSize;
//        this.up2MQQueue = queue;
//        executorService = Executors.newFixedThreadPool(poolSize, new ThreadFactoryImpl("upDataTransfer_T", false));
//        redisService = SpringContextUtil.getBean("redisService", RedisService.class);
//        kafkaProducer = SpringContextUtil.getBean("kafkaProducer", KafkaProducer.class);
//        deviceTopic = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("ennew.iot.device.topic", "enn_data_iot_metric");
//        deviceInfoTopic = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("ennew.iot.device.infoTopic", "data_iot_device_info");
//        eventTopic = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("ennew.iot.event.topic", "data_iot_event");
//        filterUnmatchMetricSwitch = Boolean.parseBoolean(SpringContextUtil.getApplicationContext().getEnvironment().getProperty("ennew.switch.filterMetric", "false"));
//    }
//
//    @Override
//    public void run() {
//        for (int i = 0; i < poolSize; i++) {
//            executorService.execute(() -> {
//                while (true) {
//                    Message message = null;
//                    try {
//                        message = up2MQQueue.take();
//                        if (message == null) {
//                            continue;
//                        }
//                        send(message);
//                        log.info("上报消息到MQ：{}", message.getMessageId());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    }

    /**
     * 异步处理上行数据
     *
     * @param message
     */
    public void handlerUpData(Message message) {

        // 异步处理上行数据
        DataThreadPool.getUpExecutorService().execute(() -> send(message));
    }

    private void send(Message msg) {
//        addMetric(msg);
        try {
            if (msg instanceof ReportRequest) {
                handleReportMsg((ReportRequest) msg);
            } else if (msg instanceof EventRequest) {
                handleEventMsg((EventRequest) msg);
            } else if (msg instanceof InfoReportRequest) {
                //info报文上报，直接转发原始报文
                com.alibaba.fastjson.JSONObject infoMessage = ((InfoReportRequest) msg).getData();
                kafkaProducer.send(deviceInfoTopic, infoMessage);
            }
        } catch (Exception e) {
            log.error("消息发送失败：{},error:{}", JSONUtil.toJsonStr(msg), e);
        }

    }

    private void handleReportMsg(ReportRequest deviceMessage) {
        String devId = deviceMessage.getDeviceId();
        KafkaNewMessage message = new KafkaNewMessage();
        message.setVersion("0.0.1");
        message.setDevId(devId);
        //下游入库要时间戳为毫秒精度
        long timeStamp = deviceMessage.getTimeStamp()!=0L?deviceMessage.getTimeStamp():System.currentTimeMillis();
        if (String.valueOf(timeStamp).length() == 10) {
            message.setTs(timeStamp * 1000);
        } else {
            message.setTs(timeStamp);
        }
        if (String.valueOf(deviceMessage.getIngestionTime()).length() == 10) {
            message.setIngestionTime(deviceMessage.getIngestionTime() * 1000);
        } else {
            message.setIngestionTime(deviceMessage.getIngestionTime());
        }
        message.setResume(StringUtils.isBlank(deviceMessage.getResume())?"N":deviceMessage.getResume());
        Map<String, Object> entries = redisService.getDeviceAttrsFromRedis(devId);
        if (entries == null) {
            //todo 无法取得设备数据缓存的异常处理
            log.warn("无设备缓存:{}", devId);
            return;
        }
        message.setDevType((String) entries.getOrDefault(RedisConstant.ENTITY_TYPE_CODE, null));
        message.setDeviceName((String) entries.getOrDefault(RedisConstant.DEVICE_NAME, null));
        message.setPeriod((String) entries.getOrDefault(RedisConstant.PERIOD, null));
        message.setSn((String) entries.getOrDefault(RedisConstant.SN, null));
        message.setProductId((String) entries.getOrDefault(RedisConstant.PRODUCT_ID, null));
        message.setTenantId((String) entries.getOrDefault(RedisConstant.TENANT_ID, null));
        message.setDeptId((String) entries.getOrDefault(RedisConstant.DEPT_ID, null));
        message.setDebug((Integer) entries.getOrDefault(RedisConstant.TEST_FLAG, 0));
        message.setSource((String) entries.getOrDefault(RedisConstant.SOURCE, null));
        message.setStaId((String) entries.getOrDefault(RedisConstant.PROJECT_CODE, null));
        message.setDomain((String) entries.getOrDefault(RedisConstant.DOMAIN, null));
        message.setDeviceCode((String) entries.getOrDefault(RedisConstant.THIRD_CODE, null));
        message.setEntityTypeName((String) entries.getOrDefault(RedisConstant.ENTITY_TYPE_NAME, null));
        message.setUploadFrequency((String) entries.getOrDefault(RedisConstant.PERIOD, null));
        String county = (String) entries.getOrDefault(RedisConstant.AREA_CODE, null);
        if (StringUtils.isNotBlank(county) && county.length() >= 6) {
            message.setProvince(county.substring(county.length() - 4) + "0000");
            message.setCity(county.substring(county.length() - 2) + "00");
            message.setCounty(county);
        }
        message.setDeviceType((String) entries.getOrDefault(RedisConstant.DEVICE_TYPE, null));
        message.setParentId((String) entries.getOrDefault(RedisConstant.PARENT_ID, null));
        //处理测点数据
        List<KafkaNewMessage.KafkaNewMetric> metrics = transformMetric(message.getProductId(), deviceMessage.getMetric(),message.getTs());
        if (CollectionUtils.isEmpty(metrics)) {
            //todo 无法取得产品元数据的异常处理
            log.warn("无测点数据缓存:{}", message.getProductId());
            return;
        }
        message.setData(metrics);
        kafkaProducer.send(deviceTopic, message);
    }

    private List<KafkaNewMessage.KafkaNewMetric> transformMetric(String productId, Map<String, Object> metricM,Long deviceTime) {
        List<KafkaNewMessage.KafkaNewMetric> metrics = new ArrayList<>();
        JSONArray measureProperties = redisService.getMeasurePsFromRedisByProduct(productId);
        if (measureProperties == null || measureProperties.isEmpty()) {
            return null;
        }
        Map<String, JSONObject> originMetricMap = null;
        if (measureProperties != null) {
            originMetricMap = measureProperties.stream().collect(Collectors.toMap(
                    e -> ((JSONObject) e).getString("code").toLowerCase(),
                    e -> (JSONObject) e,
                    (oldValue, newValue) -> oldValue)
            );
        }

        for (Map.Entry<String, Object> entry : metricM.entrySet()) {
            KafkaNewMessage.KafkaNewMetric metric = new KafkaNewMessage.KafkaNewMetric();
            //TODO 测点计算
            if (originMetricMap != null) {
                //屏蔽大小写，并补充数据
                JSONObject originMetric = originMetricMap.get(entry.getKey().toLowerCase());
                if (Objects.nonNull(originMetric)) {
                    metric.setMetric(originMetric.getString("code"));
                    metric.setValue(entry.getValue());
                    metric.setTs(deviceTime);
                    metric.setMetricUnit(originMetric.getString("unit"));
                    metric.setMetricName(originMetric.getString("name"));
                    metric.setMax(originMetric.getString("max"));
                    metric.setMin(originMetric.getString("min"));
                    metric.setType(originMetric.getString("type"));
                    metrics.add(metric);
                    continue;
                }
            }
            //过滤开关关闭 不匹配的测点，继续传递给下游
            if (!filterUnmatchMetricSwitch) {
                metric.setMetric(entry.getKey());
                metric.setValue(entry.getValue());
                metric.setTs(deviceTime);
                metrics.add(metric);
            }
        }

        //for TODO 虚拟测点计算

        return metrics;
    }

    private void handleEventMsg(EventRequest k) {
        KafkaEventMessage kafkaEventMessage = new KafkaEventMessage();
        kafkaEventMessage.setDevId(k.getDeviceId());
        kafkaEventMessage.setIdentifier(k.getIdentifier());
        kafkaEventMessage.setDevType(redisService.getDeviceTypeFromRedis(k.getDeviceId()));
        kafkaEventMessage.setProductId(redisService.getProductIdIdFromRedis(k.getDeviceId()));
        kafkaEventMessage.setDomain(redisService.getDomainFromRedis(k.getDeviceId()));
        kafkaEventMessage.setDeviceCode(redisService.getDeviceCodeFromRedis(k.getDeviceId()));
        kafkaEventMessage.setStaId(redisService.getStaIdFromRedis(k.getDeviceId()));
        kafkaEventMessage.setTs(k.getTimeStamp());
        kafkaEventMessage.setVersion(k.getVersion());
        kafkaEventMessage.setTenantId(redisService.getTenantIdFromRedis(k.getDeviceId()));
        kafkaEventMessage.setSource("custom");
        kafkaEventMessage.setType(k.getType());
        Map<String, Object> value = k.getValue();
        if (MapUtils.isNotEmpty(value) && !value.containsKey("eventStatus")) {
            value.put("eventStatus", 1);
        }
        kafkaEventMessage.setValue(value);
        kafkaProducer.send(eventTopic, kafkaEventMessage);
    }

    private void addMetric(Message msg) {
        String type;
        switch (msg.getMessageType()) {
            case INFO_REQ:
                type = "info";
                break;
            case EVENT_REQ:
                type = "event";
                break;
            case STATUS_REQ:
                type = "status";
                break;
            case HISTORY_REQ:
                type = "history";
                break;
            case REPORT_REQ:
                type = "rtg";
                break;
            default:
                return;
        }
        String transport = msg.getTransport();
        if (transport.equals(DefaultTransport.HTTP.getName())) {
            MetricCollector.httpUpCounter.labels(type).inc();
        } else if (transport.equals(DefaultTransport.TCP.getName())) {
            MetricCollector.tcpUpCounter.labels(type).inc();
        }

    }

//    private void sendToKafka(Object message) {
//        try {
//            // 异步获取发送结果
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(deviceTopic, message);
//            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//                @Override
//                public void onFailure(Throwable throwable) {
//                    log.error("{} - 生产者 发送消息失败：", deviceTopic, throwable);
//                }
//                @Override
//                public void onSuccess(SendResult<String, Object> result) {
//                    log.info("{} - 生产者 发送消息成功：{}, 内容：{}", deviceTopic, result.toString(), JSON.toJSONString(message));
//                }
//            });
//        } catch (Exception e) {
//            log.error("{} - 生产者 发送消息异常：", deviceTopic, e);
//        }
//    }

//    public void start() throws Exception {
//        new Thread(this).start();
//    }
}
