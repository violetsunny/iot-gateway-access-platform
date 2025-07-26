package com.ennew.iot.gateway.biz.session;

import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.Transport;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 11:57:08
 */
public interface ServerSession {

    AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    default AttributeKey<ServerSession> sessionKey() {
        return SESSION_KEY;
    }

    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();

    long connectTime();

    String getDeviceId();

    void ping();

    boolean isAlive();

    default void keepAlive() {
        ping();
    }

    String transport();

    /**
     * 绑定 Session 与 Channel 的 key
     */
    ServerSession bind();

    void close();

    static ServerSession newServerSession(Transport transport, Channel channel, String deviceId) {
        if (transport.getId().equals(DefaultTransport.TCP.getId())) {
            return new TcpLocalSession(channel, deviceId);
        } else {
            return new HttpLocalSession(channel, deviceId);
        }
    }
}
