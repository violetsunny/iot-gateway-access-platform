package com.ennew.iot.gateway.biz.server.cluster;

import java.util.Map;

public interface ClusterOperator {
    void init();
    void setClusterName(String clusterName);
    String getClusterName();
    void setCurrentNode(ServerNode current);
    ServerNode getCurrentNode();
    void setTimeout(long timeout);
    void putNode(ServerNode current);
    void keepAlive();
    Map<String,ServerNode> getAllNodes();
    void nodeOffline(ServerNode node);
}

