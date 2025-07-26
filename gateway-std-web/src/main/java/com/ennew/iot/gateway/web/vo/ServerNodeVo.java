package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.biz.server.cluster.ServerNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;


@Data
@AllArgsConstructor
public class ServerNodeVo implements Serializable {
    @NonNull
    private String id;
    private String name;
    private String host;
    private Map<String, Object> tags;
    private long uptime;
    private long lastKeepAlive;

    public ServerNodeVo(ServerNode node) {
        this.id = node.getId();
        this.name = node.getName();
        this.host = node.getHost();
        this.tags = node.getTags();
        this.uptime = node.getUptime();
        this.lastKeepAlive = node.getLastKeepAlive();
    }


}
