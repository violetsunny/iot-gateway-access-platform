package com.ennew.iot.gateway.biz.session;

import com.ennew.iot.gateway.biz.server.cluster.ClusterOperator;
import com.ennew.iot.gateway.biz.session.service.DeviceSessionService;
import com.ennew.iot.gateway.core.bo.DeviceSessionBo;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.integration.device.DeviceClient;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 11:55:48
 */
@Data
@Slf4j
@Component("sessionManager")
public class SessionManger {

    @Resource
    private ClusterOperator clusterOperator;
    @Resource
    private RedisService redisService;
    @Resource
    private DeviceSessionService deviceSessionService;

    private ConcurrentHashMap<String, ServerSession> sessionMap = new ConcurrentHashMap<>();

    // 单例模式
    private static SessionManger singleInstance = null;

    public static void setSingleInstance(SessionManger singleInstance) {
        SessionManger.singleInstance = singleInstance;
    }

    public static SessionManger getInstance() {
        return singleInstance;
    }

    /**
     * 增加session对象
     */
    public synchronized void addLocalSession(ServerSession session) {
        // 保存到本地Map
        String sessionId = session.getSessionId();
        sessionMap.put(sessionId, session);
        /**
         * 绑定Session
         */
        session.bind();

        String serverId = clusterOperator.getCurrentNode().getId();
        // 存储
        deviceSessionService.store(DeviceSessionBo.builder()
                .sessionId(sessionId)
                .deviceId(session.getDeviceId())
                .serverId(serverId)
                .transport(session.transport())
                .build());

    }

    //关闭连接
    public void closeSession(ChannelHandlerContext ctx) {
        ServerSession session =
                ctx.channel().attr(ServerSession.SESSION_KEY).get();

        if (null == session || session.isValid()) {
            log.error("session is null or isValid");
            return;
        }

        session.close();
        //删除本地的会话和远程会话
        deviceSessionService.remove(session.getSessionId());

        //本地：从会话集合中，删除会话
        sessionMap.remove(session.getSessionId());
        /**
         * 通知其他节点 ，用户下线
         */
//        notifyOtherImNodeOffLine(session);

    }
}
