/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.protocol.cloud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import com.ennew.iot.gateway.client.message.codec.Transport;
import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import com.ennew.iot.gateway.common.utils.CommonUtils;
import com.ennew.iot.gateway.core.parser.JsonMessagePayloadParser;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import com.ennew.iot.gateway.integration.device.model.DeviceDataRes;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import top.kdla.framework.dto.SingleResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author kanglele
 * @version $Id: CloudDeviceMessageCodec, v 0.1 2023/5/22 17:36 kanglele Exp $
 */
@Slf4j
public class CloudDeviceMessageCodec implements DeviceMessageCodec {

    private final RedisService redisService;

    private final TransmittableThreadLocal<List<? extends MetadataMapping>> data = new TransmittableThreadLocal<>();
    private final TransmittableThreadLocal<String> tenant = new TransmittableThreadLocal<>();

    public CloudDeviceMessageCodec(SpringContextUtil context) {
        redisService = SpringContextUtil.getBean("redisService", RedisService.class);
    }

    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.HTTP;
    }

    @Override
    public List<? extends Message> decode(byte[] messageBytes) {
        try {
            String json = new String(messageBytes, StandardCharsets.UTF_8);
            if (json.startsWith("{")) {
                return Collections.singletonList(parseFrom(messageBytes));
            }
            if (json.startsWith("[{")) {
                return parseFroms(messageBytes);
            }
            return null;
        } finally {
            this.tenant.remove();
            this.data.remove();
        }
    }

    @Override
    public Message parseFrom(byte[] messageBytes) {
        String json = new String(messageBytes, StandardCharsets.UTF_8);
        List<? extends MetadataMapping> metadataMappings = this.data.get();
        Map<String, Object> properties = JsonMessagePayloadParser.parseExpression(metadataMappings, JSONObject.parse(json));
        CloudMessage message = transformMessage(properties);
        message.setSource(json);
        return message;
    }

    @Override
    public byte[] toByteArray(Message message) {
        return new byte[0];
    }

    @Override
    public boolean login(LoginRequest loginRequest) {
        return false;
    }

    @Override
    public void setExt(Object... obj) {
        this.tenant.set((String) obj[0]);
        this.data.set((List) obj[1]);
    }

    @Override
    public String deviceId(String sn) {
        DeviceClient deviceClient = SpringContextUtil.getBean(DeviceClient.class);
        SingleResponse<DeviceDataRes> response = deviceClient.getBySN(sn);
        log.info("CloudDeviceMessageCodec deviceId {}", JSONObject.toJSONString(response));
        if (Objects.nonNull(response) && Objects.nonNull(response.getData())) {
            return response.getData().getId();
        }
        return (String) redisService.getValue("deviceIdSnMapping:" + this.tenant.get() + ":" + sn);
    }

    @Override
    public List<CloudMessage> parseFroms(byte[] messageBytes) {
        String json = new String(messageBytes, StandardCharsets.UTF_8);
        List<? extends MetadataMapping> metadataMappings = this.data.get();
        List<JSONObject> jsonList = JSONArray.parseArray(json, JSONObject.class);
        List<CloudMessage> cloudMessages = Lists.newArrayList();
        for (JSONObject jsonObject : jsonList) {
            Map<String, Object> properties = JsonMessagePayloadParser.parseExpression(metadataMappings, jsonObject);
            CloudMessage message = transformMessage(properties);
            message.setSource(jsonObject.toJSONString());
            cloudMessages.add(message);
        }
        return cloudMessages;
    }

    private CloudMessage transformMessage(Map<String, Object> properties) {
        CloudMessage message = new CloudMessage();
        message.setMessageId(CommonUtils.getUUID());
        message.setSn((String) properties.get("deviceId"));
        message.setDeviceId(this.deviceId((String) properties.get("deviceId")));
        Object times = properties.get("timestamp");
        Long timestamp = null;
        if (Objects.nonNull(times)) {
            timestamp = (Long) JsonMessagePayloadParser.typeOf(times, "Date");
        }
        if (Objects.isNull(timestamp)) {
            timestamp = System.currentTimeMillis();
        }
        message.setTimestamp(timestamp);
        message.setProperties(properties);
        message.setTransport(DefaultTransport.HTTP.getName());
        message.setMessageType(MessageType.REPORT_REQ);
        return message;
    }
}
