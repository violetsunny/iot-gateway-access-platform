/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.ctwing.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.ctwing.CtwingCloudServer;
import com.ennew.iot.gateway.biz.ctwing.CtwingMessage;
import com.ennew.iot.gateway.biz.ctwing.CtwingSDKOperator;
import com.ennew.iot.gateway.biz.protocol.supports.ProtocolSupports;
import com.ennew.iot.gateway.biz.queue.CacheQueue;
import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.common.enums.EsDataTypeEnum;
import com.ennew.iot.gateway.common.enums.NetworkEnum;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.core.es.ElasticSearchOperation;
import com.ennew.iot.gateway.core.es.index.EsDataEvent;
import com.ennew.iot.gateway.core.help.CloudCloudHelp;
import com.ennew.iot.gateway.core.repository.TripartitePlatformInfoRepository;
import com.ennew.iot.gateway.core.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kdla.framework.dto.Response;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author kanglele
 * @version $Id: CtwingCloudServerImpl, v 0.1 2023/11/15 15:56 kanglele Exp $
 */
@Service
@Slf4j
public class CtwingCloudServerImpl implements CtwingCloudServer {

    @Resource
    private CloudCloudHelp ctwingCloudCloudHelp;
    @Resource
    private RedisService redisService;
    @Resource
    private ProtocolSupports protocolSupports;
    @Resource
    private ElasticSearchOperation elasticSearchOperation;
    @Resource
    private TripartitePlatformInfoRepository tripartitePlatformInfoRepository;

    @Autowired
    private UpDataTransfer upDataTransfer;

//    private static final String BEAT_HEART = "67CCED";

    public void dealCloudData(String msg) throws Exception {
        CtwingMessage ctwingMessage = (CtwingMessage) ctwingCloudCloudHelp.transcoding(msg);
        log.info("CtwingCloudServer 电信云处理云对云数据 {}", JSON.toJSONString(ctwingMessage));
        EsDataEvent esDataEvent = new EsDataEvent(ctwingMessage.getDeviceId(), EsDataTypeEnum.rtg.getType(), msg);
        elasticSearchOperation.saveEsLog(esDataEvent);

        CtwingMessage.PayloadDTO payload = ctwingMessage.getPayload();
        if (payload != null && "dataReport".equals(ctwingMessage.getMessageType())) {
//            String data = payload.getAPPdata();
//            byte[] dedata = ctwingCloudCloudHelp.decode(data);
//            String decdata = ctwingCloudCloudHelp.decrypt(dedata);
//            log.info("电信云解密后数据：{}", decdata);
//            if (BEAT_HEART.equals(decdata)) {
//                //心跳
//                return;
//            }
            //外部设备id
//            String sn = getSn(dedata);
            //TODO 转成恩牛设备
            String deviceId = redisService.getThirdCloudDeviceId(ctwingMessage.getDeviceId());
            if (StringUtils.isBlank(deviceId)) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "电信云获取恩牛设备id失败：" + ctwingMessage.getDeviceId());
            }
            //TODO 转成恩牛产品
            String productId = redisService.getProductIdIdFromRedis(deviceId);
            if (StringUtils.isBlank(productId)) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "电信云获取恩牛产品id失败：" + deviceId);
            }
            //TODO 转成产品找协议id
            List<Object> protocol = redisService.getThirdCloudProductId(ctwingMessage.getProductId());
            if (CollectionUtils.isEmpty(protocol) || StringUtils.isBlank((String) protocol.get(0))) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "电信云获取协议id失败：" + ctwingMessage.getProductId());
            }
            //上行
            String protocolId = (String) protocol.get(0);
            ProtocolSupport protocolSupport = protocolSupports.getProtocol(protocolId);
            if (Objects.isNull(protocolSupport)) {
                throw new BizException(ErrorCode.BIZ_ERROR, "电信云未找到解析协议");
            }
            DeviceMessageCodec deviceMessageCodec = protocolSupport.getMessageCodec(DefaultTransport.HTTP);
            if (Objects.isNull(deviceMessageCodec)) {
                throw new BizException(ErrorCode.BIZ_ERROR, "电信云协议解析错误");
            }

            //上行
            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setMessageType(MessageType.REPORT_REQ);
            reportRequest.setMetric(JSON.parseObject(msg));
            ReportRequest message = (ReportRequest) deviceMessageCodec.parseFrom(JSON.toJSONBytes(reportRequest));
            if (message == null || MapUtils.isEmpty(message.getMetric())) {
                log.warn("电信云无有效上传测点:{},{}", ctwingMessage.getDeviceId(), deviceId);
                return;
            }
            message.setIngestionTime(System.currentTimeMillis());
            message.setDeviceId(deviceId);
            message.setTimeStamp(ctwingMessage.getTimestamp());
            dealMessageBody(message);

            //TODO 应答
            if (StringUtils.isBlank(message.getResponse())) {
                throw new BizException(ErrorCode.BIZ_ERROR, "电信云解析应答数据为空");
            }
            String masterApiKey = (String) protocol.get(2);
//            String masterApiKey = "9e86aa2f73384a74b1653d74734871c4";
            if (StringUtils.isBlank(masterApiKey)) {
                throw new BizException(ErrorCode.BIZ_ERROR, "电信云的masterApiKey为空");
            }

            TripartitePlatformInfoBo bo = tripartitePlatformInfoRepository.queryByCode(NetworkEnum.CTWING.getCode().toLowerCase());
            if (MapUtils.isEmpty(bo.getContent())) {
                throw new BizException(ErrorCode.BIZ_ERROR, "电信云的appKey为空");
            }
            String appKey = bo.getContent().get("appKey");
            String appSecret = bo.getContent().get("appSecret");
//            String appKey = "G1P0bqA1lv1";
//            String appSecret = "Drkg25ehhb";
            JSONObject jsonObject = JSONObject.parseObject(message.getResponse());
            CtwingSDKOperator ctwingSDKOperator = new CtwingSDKOperator(appKey, appSecret);
            Response response = ctwingSDKOperator.sendDeviceCommand(masterApiKey, ctwingMessage.getProductId(), ctwingMessage.getDeviceId(), jsonObject.getString("command"), null, 2, null);
            log.info("电信云应答返回：{}", JSON.toJSONString(response));

        }

    }

    private void dealMessageBody(ReportRequest message) {
        try {
            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setDeviceId(message.getDeviceId());
            reportRequest.setMessageId(String.format("%s%s", message.getDeviceId(), message.getMessageId()));
            reportRequest.setMessageType(message.getMessageType());
            reportRequest.setTransport(DefaultTransport.HTTP.getName());
            reportRequest.setTimeStamp(message.getTimeStamp());
            reportRequest.setIngestionTime(message.getIngestionTime());
            reportRequest.setMetric(message.getMetric());
            upDataTransfer.handlerUpData(message);
        } catch (Exception e) {
            log.error("数据缓存队列异常", e);
        }
    }

}
