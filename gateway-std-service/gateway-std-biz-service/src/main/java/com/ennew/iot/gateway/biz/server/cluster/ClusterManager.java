package com.ennew.iot.gateway.biz.server.cluster;

import com.ennew.iot.gateway.biz.session.service.DeviceSessionService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ClusterManager {
    private final ClusterOperator operator;
    private final long timeout;

    @Resource
    private DeviceSessionService deviceSessionService;

    public ClusterManager(String clusterName, long timeout, ServerNode current, ClusterOperator operator) {
        this.timeout = timeout;
        this.operator = operator;
        operator.setClusterName(clusterName);
        operator.setCurrentNode(current);
        operator.setTimeout(timeout);
    }

    public void startup() {
        operator.init();
    }

    public void shutdown() {
        operator.nodeOffline(operator.getCurrentNode());
        deviceSessionService.removeServerId(operator.getCurrentNode().getId());
    }

    public void checkAlive() {
        //检查集群节点
        Map<String, ServerNode> clusterNodes = operator.getAllNodes();
        List<ServerNode> maybeOffline = new ArrayList<>();
        for (ServerNode serverNode : clusterNodes.values()) {
            //检查可能离线的节点
            if (System.currentTimeMillis() - serverNode.getLastKeepAlive() > timeout) {
                maybeOffline.add(serverNode);
            }
        }
        //处理可能离线的节点：清理本地节点缓存, 并广播该节点的离线消息
        for (ServerNode serverNode : maybeOffline) {
            if(!serverNode.getId().equals(getCurrentNode().getId())){
                operator.nodeOffline(serverNode);
            }
        }
        //更新自身 并广播在线消息
        operator.keepAlive();
    }

    public ServerNode getCurrentNode() {
        return operator.getCurrentNode();
    }
    public List<ServerNode> getAllNode() {
        return new ArrayList(operator.getAllNodes().values());
    }

}
