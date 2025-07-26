package com.ennew.iot.gateway.biz.server.cluster;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;

@Data
public class ServerNode implements Serializable {
    @NonNull
    private String id;
    private String name;
    private String host;
    private Map<String, Object> tags;
    private boolean leader;
    private long uptime;
    private long lastKeepAlive;

    public ServerNode(String id, String name, String host) {
        if (name == null || name.equals("")) {
            name = id;
        }
        this.id = id;
        this.name = name;
        this.host = host;
        this.uptime = System.currentTimeMillis();
        this.lastKeepAlive = this.uptime;
    }

    public ServerNode(String id, String name, String host, Map tags) {
        if (name == null || name.equals("")) {
            name = id;
        }
        this.id = id;
        this.name = name;
        this.host = host;
        this.tags = tags;
        this.uptime = System.currentTimeMillis();
        this.lastKeepAlive = this.uptime;
    }
}
