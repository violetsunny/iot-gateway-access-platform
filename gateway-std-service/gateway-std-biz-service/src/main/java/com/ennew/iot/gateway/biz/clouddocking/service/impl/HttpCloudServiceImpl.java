/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.clouddocking.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.clouddocking.service.CloudDockingService;
import com.ennew.iot.gateway.biz.clouddocking.service.HttpCloudService;
import com.ennew.iot.gateway.biz.protocol.cloud.CloudMessage;
import com.ennew.iot.gateway.biz.queue.CacheQueue;
import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.common.enums.EsDataTypeEnum;
import com.ennew.iot.gateway.core.bo.CloudDockingBodyBO;
import com.ennew.iot.gateway.core.es.ElasticSearchOperation;
import com.ennew.iot.gateway.core.es.index.EsDataEvent;
import com.ennew.iot.gateway.core.message.CloudWorkDataMessage;
import com.ennew.iot.gateway.core.service.KafkaProducer;
import com.google.common.collect.Lists;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.kdla.framework.common.aspect.watch.StopWatchWrapper;
import top.kdla.framework.common.help.ThreadPoolHelp;
import top.kdla.framework.supplement.http.VertxHttpClient;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author kanglele
 * @version $Id: HttpCloudServerImpl, v 0.1 2023/5/19 16:49 kanglele Exp $
 */
@Service
@Slf4j
public class HttpCloudServiceImpl implements HttpCloudService {

    @Resource
    private VertxHttpClient vertxHttpClient;
    @Resource
    private KafkaProducer kafkaProducer;
    @Resource
    private ProtocolSupport cloudProtocol;
    @Resource
    private CloudDockingService cloudDockingService;
    @Resource
    private ExecutorService executorService;
    @Resource
    private ElasticSearchOperation elasticSearchOperation;

    @Autowired
    private UpDataTransfer upDataTransfer;

    @Value("${spring.kafka.consumer.topics-cloud:iot-gateway-cloud-data}")
    private String cloudTopic;

    @Override
    @StopWatchWrapper(logHead = "cloud", msg = "操作")
    @Transactional(rollbackFor = Exception.class)
    public void executeWork(String tenant, List<String> productIds) throws Exception {
        //根据tenant取出对应的配置，并进行校验
        List<CloudDockingBodyBO> bodyBos = cloudDockingService.createHttpRequest(tenant);
        if (CollectionUtils.isEmpty(bodyBos)) {
            return;
        }

        //根据配置组装请求
        List<CloudWorkDataMessage> messages = transformMessages(bodyBos, tenant);
        Map<String, List<CloudWorkDataMessage>> messagesMap = messages.stream().collect(Collectors.groupingBy(CloudWorkDataMessage::getGroup));
        for (Map.Entry<String, List<CloudWorkDataMessage>> v : messagesMap.entrySet()) {
            //根据配置的cloud限流策略，进行并发处理，可以考虑消费组中消费者数量（也就是集群中设备数）
            //10个并发，有3个数据。那就应该发3次kafka
            Integer limit = v.getValue().get(0).getLimit();
            if (limit == null || limit <= 0) {
                limit = v.getValue().size();
            }

            if (v.getValue().size() == 1) {
                ((HttpCloudServiceImpl) AopContext.currentProxy()).dealCloudData(v.getValue().get(0));
            } else {
                List<List<CloudWorkDataMessage>> messageList = Lists.partition(v.getValue(), limit);//1-3

                executorService.execute(new Runnable() {

                    @Override
                    public void run() {
                        messageList.forEach(msgs -> {
                            msgs.forEach(msg -> {
                                //dealCloudData
                                kafkaProducer.send(cloudTopic, msg);
                            });
                            try {
                                //等待2秒后再发
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                log.warn("InterruptedException", e);
                            }
                        });
                    }
                });

            }
        }
    }

    private List<CloudWorkDataMessage> transformMessages(List<CloudDockingBodyBO> bodys, String tenant) {
        List<CloudWorkDataMessage> messages = Lists.newArrayList();
        bodys.forEach(body -> {
            CloudWorkDataMessage message = new CloudWorkDataMessage();
            message.setUrl(body.getUrl());
            message.setMetadataMapping(body.getMetadataMapping());
            message.setHttpMethod(body.getMethod());
            message.setHeaders(body.getHeader());
            message.setReq(body.getBody());
            message.setResRoot(body.getRootPath());
            message.setGroup(body.getGroup());
            message.setTenant(tenant);
            messages.add(message);
        });
        return messages;
    }

    @Override
    @StopWatchWrapper(logHead = "cloud", msg = "处理cloud返回数据")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public void dealCloudData(CloudWorkDataMessage message) throws Exception {
        log.info("dealCloudData 处理云对云数据 {}", JSON.toJSONString(message));
        //调用外部接口
        CompletableFuture<JSONObject> future = vertxHttpClient.sendRequest(HttpMethod.valueOf(message.getHttpMethod().toUpperCase(Locale.ROOT)), message.getUrl(), message.getHeaders(), message.getReq(), JSONObject.class);
        JSONObject obj = future.get();
        log.info("dealCloudData 处理云对云数据 返回 obj:{}", JSON.toJSONString(obj));
        if (Objects.isNull(obj) || Objects.isNull(obj.get(message.getResRoot()))) {
            log.warn("dealCloudData 处理云对云数据 返回数据空");
            return;
        }
        //根据配置取出数据,协议转换解析数据
//        ProtocolSupport protocolSupport = protocolSupports.getProtocol("cloudProtocol");
//        if (Objects.isNull(protocolSupport)) {
//            throw new BizException(ErrorCode.BIZ_ERROR, "未找到解析协议");
//        }
        DeviceMessageCodec deviceMessageCodec = cloudProtocol.getMessageCodec(DefaultTransport.HTTP);
        if (Objects.isNull(deviceMessageCodec)) {
            log.warn("dealCloudData 处理云对云数据 没有对应解析协议");
            return;
        }

        deviceMessageCodec.setExt(message.getTenant(), message.getMetadataMapping());
        List<CloudMessage> cloudMessages = (List<CloudMessage>) deviceMessageCodec.decode(JSON.toJSONString(obj.get(message.getResRoot())).getBytes(StandardCharsets.UTF_8));
        //数据处理和发送数据给下游
        for (CloudMessage cloudMessage : cloudMessages) {
            //ES
            EsDataEvent esDataEvent = new EsDataEvent(cloudMessage.getSn(), EsDataTypeEnum.rtg.getType(), cloudMessage.getDeviceId(), cloudMessage.getSource());
            elasticSearchOperation.saveEsLog(esDataEvent);
            if (MapUtils.isEmpty(cloudMessage.getProperties())) {
                log.warn("无有效上传测点:{}", cloudMessage.getDeviceId());
                continue;
            }
            //转发数据
            dealMessageBody(cloudMessage);
        }

    }

    private void dealMessageBody(CloudMessage message) {
        try {
            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setDeviceId(message.getDeviceId());
            reportRequest.setMessageId(String.format("%s%s", message.getDeviceId(), message.getMessageId()));
            reportRequest.setMessageType(message.getMessageType());
            reportRequest.setTransport(DefaultTransport.HTTP.getName());
            reportRequest.setTimeStamp(message.getTimestamp());
            reportRequest.setIngestionTime(System.currentTimeMillis());
            reportRequest.setMetric(message.getProperties());
            upDataTransfer.handlerUpData(reportRequest);
        } catch (Exception e) {
            log.error("数据缓存队列异常", e);
        }
    }

}
