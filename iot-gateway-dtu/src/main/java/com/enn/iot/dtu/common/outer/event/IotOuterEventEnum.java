package com.enn.iot.dtu.common.outer.event;

import lombok.Getter;

public enum IotOuterEventEnum {
    /**
     * 客户端已连接事件
     */
    CLIENT_CONNECTED("client.connected"),
    /**
     * 客户端已断开连接事件
     */
    CLIENT_DISCONNECTED("client.disconnected");

    /**
     * 事件编码
     */
    @Getter
    private final String code;

    IotOuterEventEnum(String code) {
        this.code = code;
    }
}
