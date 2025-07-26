package com.ennew.iot.gateway.biz.server.process;

import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
import com.ennew.iot.gateway.biz.session.ServerSession;
import com.ennew.iot.gateway.biz.session.TcpLocalSession;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.Protocol;
import com.ennew.iot.gateway.biz.queue.CacheQueue;
import com.ennew.iot.gateway.biz.session.SessionManger;
import com.ennew.iot.gateway.client.protocol.model.LoginResponse;
import com.ennew.iot.gateway.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 12:34:02
 */
@Slf4j
@Component("loginProcesser")
public class LoginProcesser {

    @Autowired
    private SessionManger sessionManger;

    @Autowired
    private UpDataTransfer upDataTransfer;

    public Boolean action(ProtocolSupport protocol, ServerSession session, LoginRequest loginRequest) {
        // 验证deviceId
        if (null == loginRequest.getDeviceId() && !protocol.getMessageCodec(DefaultTransport.TCP).login(loginRequest)) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessageId(CommonUtils.getUUID());
            loginResponse.setResult(false);
            loginResponse.setTimeStamp(System.currentTimeMillis());
            session.writeAndFlush(loginResponse);
            return false;
        }

        sessionManger.addLocalSession(session);

        try {
            upDataTransfer.handlerUpData(loginRequest);
        } catch (Exception e) {
            log.error("登录缓冲消息失败", e);
        }

        /**
         * 响应登录成功
         */
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessageId(CommonUtils.getUUID());
        loginResponse.setDeviceId(loginRequest.getDeviceId());
        loginResponse.setResult(true);
        loginResponse.setTimeStamp(System.currentTimeMillis());
        session.writeAndFlush(loginResponse);
        return true;
    }

}
