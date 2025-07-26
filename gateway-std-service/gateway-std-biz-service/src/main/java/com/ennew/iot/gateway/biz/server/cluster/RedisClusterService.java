package com.ennew.iot.gateway.biz.server.cluster;

import com.ennew.iot.gateway.biz.protocol.supports.RedisTopicRegistry;
import com.ennew.iot.gateway.core.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RedisClusterService extends RedisService implements ClusterOperator, MessageListener {
    private String clusterName;
    private String hashName="__ha_all_node:";
    public static String ONLINE_TOPIC="__ha_online_topic:";
    public static String OFFLINE_TOPIC="__ha_offline_topic:";
    private final Map<String, ServerNode> allNode = new ConcurrentHashMap();
    private ServerNode current;
    private long timeout;
    @Autowired
    RedisTopicRegistry redisTopicRegistry;

    @Override
    public void init() {
        hashName += clusterName;
        ONLINE_TOPIC += clusterName;
        OFFLINE_TOPIC += clusterName;
        redisTopicRegistry.register(this, PatternTopic.of(ONLINE_TOPIC),PatternTopic.of(OFFLINE_TOPIC));
        updateAllNodeFromRedis();
    }

    @Override
    public void putNode(ServerNode current) {
        Map<String, ServerNode> clusterNodes = (Map<String, ServerNode>) getValue(hashName);
        if (clusterNodes == null) {
            clusterNodes = new HashMap<>();
        }
        clusterNodes.put(current.getId(), current);
        putValueDuration(hashName, clusterNodes, Duration.ofMillis(timeout));
    }

    @Override
    public void keepAlive() {
        log.debug("node keep alive:{}", current.getName());
        current.setLastKeepAlive(System.currentTimeMillis());
        putNode(current);
        publish(ONLINE_TOPIC, current);
    }

    private void updateAllNodeFromRedis(){
        allNode.put(current.getId(), current);
        Map<String, ServerNode> clusterNodes = (Map<String, ServerNode>) getValue(hashName);
        if (clusterNodes == null) {
            clusterNodes = new HashMap<>();
        }
        clusterNodes.forEach((k, v) -> allNode.put(k, v));
    }
    @Override
    public Map<String, ServerNode> getAllNodes() {
        return allNode;
    }

    @Override
    public void nodeOffline(ServerNode node) {
        allNode.remove(node.getId());
        Map<String, ServerNode> clusterNodes = (Map<String, ServerNode>) getValue(hashName);
        if (clusterNodes == null) {
            return;
        }
        clusterNodes.remove(node.getId());
        putValueDuration(hashName, clusterNodes, Duration.ofSeconds(5));
        publish(OFFLINE_TOPIC, node);
    }

    private void electionLeader() {
        allNode.values()
                .stream()
                .peek(serverNode -> serverNode.setLeader(false))
                .min(Comparator.comparing(ServerNode::getUptime))
                .ifPresent(serverNode -> serverNode.setLeader(true));
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        //接收订阅的消息
        String topic = getTemplate().getStringSerializer().deserialize(message.getChannel());
        if (topic.equals(ONLINE_TOPIC)) {
            //处理节点在线消息 仅更新本地节点信息
            ServerNode node = (ServerNode) getTemplate().getValueSerializer().deserialize(message.getBody());
            if (!node.getId().equals(current.getId())) {
                allNode.put(node.getId(), node);
            }
            log.debug("{}节点在线", node);
        } else if (topic.equals(OFFLINE_TOPIC)) {
            //处理节点离线消息 删除redis节点并执行本地节点清理
            ServerNode node = (ServerNode) getTemplate().getValueSerializer().deserialize(message.getBody());
            if (!node.getId().equals(current.getId())) {
                allNode.put(node.getId(), node);
                Map<String, ServerNode> clusterNodes = (Map<String, ServerNode>) getValue(hashName);
                if (clusterNodes == null) {
                    return;
                }
                clusterNodes.remove(node.getId());
                putValueDuration(hashName, clusterNodes, Duration.ofSeconds(5));
                allNode.remove(node.getId());
                log.debug("{}节点离线", node);
            }
        }
    }

    @Override
    public void setCurrentNode(ServerNode current) {
        this.current = current;
    }

    @Override
    public ServerNode getCurrentNode() {
        return current;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

}
