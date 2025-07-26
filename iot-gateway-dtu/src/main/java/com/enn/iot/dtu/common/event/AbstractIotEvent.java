package com.enn.iot.dtu.common.event;

import lombok.Data;

@Data
public abstract class AbstractIotEvent {

    /**
     * 事件类型
     */
    protected String type;
    /**
     * 事件编码
     *
     */
    protected String code;
    /**
     * 事件描述
     */
    protected String message;
    /**
     * 事件时间，单位 ms
     */
    protected Long timeMs;

    protected AbstractIotEvent() {
        this.timeMs = System.currentTimeMillis();
    }
}
