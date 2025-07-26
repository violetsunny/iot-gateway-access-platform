package com.ennew.iot.gateway.biz.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.gateway.service.HttpGatewayService;
import com.ennew.iot.gateway.biz.protocol.supports.ProtocolSupports;
import com.ennew.iot.gateway.biz.queue.CacheQueue;
import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.EventRequest;
import com.ennew.iot.gateway.client.protocol.model.InfoReportRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.client.utils.JsonUtil;
import com.ennew.iot.gateway.common.enums.EsDataTypeEnum;
import com.ennew.iot.gateway.common.utils.CommonUtils;
import com.ennew.iot.gateway.core.bo.HttpEventDataBo;
import com.ennew.iot.gateway.core.bo.HttpGatewayStatusBo;
import com.ennew.iot.gateway.core.bo.HttpProtocolMessageBO;
import com.ennew.iot.gateway.core.es.ElasticSearchOperation;
import com.ennew.iot.gateway.core.es.index.EsDataEvent;
import com.ennew.iot.gateway.core.es.index.EsSourceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kdla.framework.common.help.DateHelp;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @Author: alec
 * Description:
 * @date: 下午3:18 2023/4/27
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class HttpGatewayServiceImpl implements HttpGatewayService {

    private static final String TRANSPORT = DefaultTransport.HTTP.getName();

    private final ProtocolSupports protocolSupports;

//    private final KafkaProducer kafkaProducer;

    private final ElasticSearchOperation elasticSearchOperation;

//    @Value("${ennew.kafka.esLogTopic:data_iot_es_log}")
//    private String esLogTopic;

    @Autowired
    private UpDataTransfer upDataTransfer;


    @Override
    public void realDataReporting(String productId, List<HttpProtocolMessageBO> messageList) {
        messageList.forEach(res -> dealMessageBody(productId, res));
    }

    @Override
    public void realDataReporting(String productId, String pKey, List<HttpProtocolMessageBO> messageList) {
        //需要根据产品id 查询对于协议ID
        ProtocolSupport protocolSupport = protocolSupports.getProtocol(productId);
        if (Objects.isNull(protocolSupport)) {
            throw new BizException(ErrorCode.BIZ_ERROR, "未找到解析协议");
        }
        DeviceMessageCodec deviceMessageCodec = protocolSupport.getMessageCodec(DefaultTransport.HTTP);
        if (Objects.isNull(deviceMessageCodec)) {
            throw new BizException(ErrorCode.BIZ_ERROR, "协议解析错误");
        }
        messageList.forEach(res -> {
            ReportRequest reportRequest = buildReportRequest(pKey, res);
            Message message = deviceMessageCodec.parseFrom(JsonUtil.object2JsonBytes(reportRequest));
//            try {
//                CacheQueue.up2MQQueue.put(message);
//            } catch (Exception e) {
//                log.error("数据缓存队列异常", e);
//            }
            upDataTransfer.handlerUpData(message);
        });
    }

    @Override
    public void historyDataReporting(String productId, String pKey, List<HttpProtocolMessageBO> messageList) {
        realDataReporting(productId, pKey, messageList);
    }

    @Override
    public Boolean infoDataReporting(JSONObject data) {
        //info报文上报到kafka topic为 data_iot_device_info 报文格式与rtg链路不同
        //todo 发送到es
        EsDataEvent esDataEvent = new EsDataEvent(data.getString("sn"), EsDataTypeEnum.info.getType(), JSON.toJSONString(data));
        elasticSearchOperation.saveEsLog(esDataEvent);

        try {
            InfoReportRequest reportRequest = buildInfoReportRequest(data);
            upDataTransfer.handlerUpData(reportRequest);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("数据缓存队列异常", e);
            return Boolean.FALSE;
        }

    }

    private InfoReportRequest buildInfoReportRequest(JSONObject data) {
        InfoReportRequest reportRequest = new InfoReportRequest();
        reportRequest.setDeviceId(data.getString("sn"));
        reportRequest.setMessageId(CommonUtils.getUUID());
        reportRequest.setMessageType(MessageType.INFO_REQ);
        reportRequest.setTransport(TRANSPORT);
        reportRequest.setData(data);
        return reportRequest;
    }

    @Override
    public Boolean statusDataReporting(HttpGatewayStatusBo httpGatewayStatusData) {
        //status报文走rtg链路 报文格式相同
        String pKey = httpGatewayStatusData.getPKey();
        String sn = httpGatewayStatusData.getSn();
        Long ts = httpGatewayStatusData.getTs();
        //todo 发送到es
        EsDataEvent esDataEvent = new EsDataEvent(sn, EsDataTypeEnum.status.getType(), JSON.toJSONString(httpGatewayStatusData));
        elasticSearchOperation.saveEsLog(esDataEvent);

        HttpProtocolMessageBO message = new HttpProtocolMessageBO(sn, ts, httpGatewayStatusData.getData());
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDeviceId(message.getDev());
        reportRequest.setMessageId(CommonUtils.getUUID());
        reportRequest.setMessageType(MessageType.STATUS_REQ);
        reportRequest.setTransport(TRANSPORT);
        reportRequest.setTimeStamp(message.getTs());
        reportRequest.setIngestionTime(System.currentTimeMillis());
        reportRequest.setMetric(message.getMetric());
        try {
            upDataTransfer.handlerUpData(reportRequest);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("数据缓存队列异常", e);
            return Boolean.FALSE;
        }
    }

    private ReportRequest buildReportRequest(String productId, HttpProtocolMessageBO message) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDeviceId(message.getDev());
        reportRequest.setMessageId(CommonUtils.getUUID());
        reportRequest.setMessageType(MessageType.REPORT_REQ);
        reportRequest.setTransport(TRANSPORT);
        reportRequest.setTimeStamp(message.getTs() == null ? System.currentTimeMillis() : message.getTs());
        reportRequest.setIngestionTime(System.currentTimeMillis());
        reportRequest.setMetric(message.getMetric());
        reportRequest.setResume(message.getResume());
        return reportRequest;
    }

    private void dealMessageBody(String productId, HttpProtocolMessageBO message) {
        try {
            ReportRequest reportRequest = buildReportRequest(productId, message);
            upDataTransfer.handlerUpData(reportRequest);
        } catch (Exception e) {
            log.error("数据缓存队列异常", e);
        }
    }


    @Override
    public Boolean eventReport(HttpEventDataBo httpEventDataBo) throws Exception {
        //ES
        EsDataEvent esDataEvent = new EsDataEvent(httpEventDataBo.getSn(), EsDataTypeEnum.event.getType(), JSON.toJSONString(httpEventDataBo));
        elasticSearchOperation.saveEsLog(esDataEvent);
//        kafkaProducer.send(esLogTopic, esDataEvent);

        //process
        httpEventDataBo.getDevs().forEach(dev -> {
            EventRequest request = buildEventRequest(httpEventDataBo, dev);
            upDataTransfer.handlerUpData(request);

        });

        return Boolean.TRUE;
    }

    private EsSourceData buildEsSourceData(HttpEventDataBo httpEventDataBo) {
        String index = "custom_" + EsDataTypeEnum.event.getEsIndexType() + "_" + DateHelp.format(new Date(), "yyyy-MM-dd-HH");
        return new EsSourceData(httpEventDataBo.getSn(), EsDataTypeEnum.event.getType(), index, JSON.toJSONString(httpEventDataBo));
    }

    private EventRequest buildEventRequest(HttpEventDataBo eventDataBo, HttpEventDataBo.Devs dev) {
        EventRequest request = new EventRequest();
        request.setDeviceId(dev.getDeviceId());
        request.setMessageId(dev.getDeviceId() + "" + dev.getTs());
        request.setTransport(DefaultTransport.HTTP.getName());
        request.setVersion(eventDataBo.getVersion());
        request.setType(dev.getEventType());
        request.setTimeStamp(dev.getTs());
        request.setIdentifier(dev.getIdentifier());
        request.setValue(dev.getValue());
        return request;
    }

}
