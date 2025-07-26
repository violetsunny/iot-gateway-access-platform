package com.ennew.iot.gateway.biz.session;

import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.Transport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 12:17:46
 */
@Slf4j
@Data
public class HttpLocalSession implements ServerSession {

    private static final Transport transport = DefaultTransport.HTTP;

    /**
     * 绑定 Session 与 Channel 的 key
     */
//    public static final AttributeKey<HttpLocalSession> SESSION_KEY =
//            AttributeKey.valueOf("SESSION_KEY");

    /**
     * Netty Channel
     */
    private Channel channel;

    /**
     * Session唯一ID, port + deviceId
     */
    private final String sessionId;

    /**
     * 设备id
     */
    private final String deviceId;

    @Setter
    private boolean closed;

    /**
     * 登录状态
     */
    private boolean isLogin = false;

    /**
     * 连接时间
     */
    private final long connectTime = System.currentTimeMillis();

    private long lastPingTime = System.currentTimeMillis();

    private final long keepaliveTimeout = Duration.ofMinutes(10).toMillis();

    public HttpLocalSession(Channel channel, String deviceId) {
        this.channel = channel;
        this.sessionId = sessionId(channel,deviceId);
        this.deviceId = deviceId;
    }

    public HttpLocalSession bind() {
        log.info("LocalSession 绑定 Channel：{}", channel.remoteAddress());
        channel.attr(SESSION_KEY).set(this);
        return this;
    }

    @Override
    public void writeAndFlush(Object pkg) {
        if (channel.isWritable()) {
            channel.writeAndFlush(pkg);
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public static String sessionId(Channel channel, String deviceId) {
        InetSocketAddress address = (InetSocketAddress)channel.localAddress();
        return address.getPort() + ":" +deviceId;
    }

    @Override
    public boolean isValid() {
        return false;
    }


    //关闭连接
    public synchronized void close() {
        //用户下线 通知其他节点
        this.closed = true;
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("Channel 关闭异常");
                }
            }
        });
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    @Override
    public void ping() {
        lastPingTime = System.currentTimeMillis();
    }

    @Override
    public boolean isAlive() {
        return !closed && System.currentTimeMillis() - lastPingTime < keepaliveTimeout;
    }

    @Override
    public String transport(){
        return transport.getName();
    }

    @Override
    public AttributeKey<ServerSession> sessionKey() {
        return SESSION_KEY;
    }
}
