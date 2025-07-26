package com.ennew.iot.gateway.biz.server.handler;

import com.ennew.iot.gateway.biz.concurrent.CallbackTask;
import com.ennew.iot.gateway.biz.concurrent.CallbackTaskScheduler;
import com.ennew.iot.gateway.biz.server.process.LoginProcesser;
import com.ennew.iot.gateway.biz.session.ServerSession;
import com.ennew.iot.gateway.biz.session.TcpLocalSession;
import com.ennew.iot.gateway.biz.session.SessionManger;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.protocol.Protocol;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.LoginRequest;
import com.ennew.iot.gateway.client.protocol.model.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hanyilong@enn.cn
 */
@Slf4j
@Service
@Data
@ChannelHandler.Sharable
public class LoginHandler extends ChannelInboundHandlerAdapter {

    private ProtocolSupport protocol;

    @Autowired
    private LoginProcesser loginProcesser;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        Message message = (Message) msg;
        MessageType type = message.getMessageType();
        if (type.equals(MessageType.LOGIN_REQ)) {
            // 处理登录消息
            ServerSession tcpLocalSession = ServerSession.newServerSession(DefaultTransport.TCP,ctx.channel(), message.getDeviceId());

            CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
                @Override
                public Boolean execute() throws Exception {
                    Boolean result = false;
                    try {
                        System.out.println("aaaaaaaaaaaaaaaa");
                        result = loginProcesser.action(protocol, tcpLocalSession, (LoginRequest) message);
                        System.out.println("bbbbbbbbbbbbbbbbb");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return result;
                }

                @Override
                public void onBack(Boolean result) {
                    if (result) {
                        ctx.pipeline().remove(LoginHandler.this);
                        log.info("登录成功：{}", tcpLocalSession.getSessionId());
                    } else {
                        SessionManger.getInstance().closeSession(ctx);
                        log.info("登录失败：{}", tcpLocalSession.getSessionId());
                    }
                }

                @Override
                public void onException(Throwable t) {
                    log.info("登录出现问题了：{}", tcpLocalSession.getSessionId());
                }
            });
        }else {
            ctx.fireChannelRead(msg);
        }

    }

}
