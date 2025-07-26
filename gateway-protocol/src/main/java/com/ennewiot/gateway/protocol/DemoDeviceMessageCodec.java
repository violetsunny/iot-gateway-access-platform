package com.ennewiot.gateway.protocol;

import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.*;
import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.model.OperationResponse;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.client.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public class DemoDeviceMessageCodec implements DeviceMessageCodec {
    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.TCP;
    }

    @Override
    public Message parseFrom(byte[] messageBytes) {
        Message msg = JsonUtil.jsonBytes2Object(messageBytes,Message.class);
        Assert.notNull(msg, "msg must not be null");
        Assert.notNull(msg.getMessageType(), "messageType must not be null");
        if(msg.getMessageType() == MessageType.LOGIN_REQ){
            msg = JsonUtil.jsonBytes2Object(messageBytes, LoginRequest.class);
        }else if(msg.getMessageType() == MessageType.REPORT_REQ){
            msg = JsonUtil.jsonBytes2Object(messageBytes, ReportRequest.class);
        }else if(msg.getMessageType() == MessageType.OPERATION_RSP){
            msg = JsonUtil.jsonBytes2Object(messageBytes, OperationResponse.class);
        }
        msg.setTransport(getSupportTransport().getName());
        return msg;
    }

    @Override
    public byte[] toByteArray(Message message) {
        return JsonUtil.object2JsonBytes(message);
    }

    @Override
    public boolean login(LoginRequest loginRequest) {
        return true;
    }

}
