package com.enn.iot.dtu.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IotResponseTimeoutEvent extends AbstractIotEvent {
    /**
     * 等待应答时间
     */
    private Long afterWriteTimeMs;
    /**
     * 超时配置
     */
    private Long responseTimeoutMs;
    /**
     * 上次写时间
     */
    private Long lastWriteTimeMs;
    /**
     * 上次读时间
     */
    private Long lastReadTimeMs;

    public IotResponseTimeoutEvent() {
        super();
        this.type = "ResponseTimeout";
    }

    public static IotResponseTimeoutEvent instance(Long afterWriteTimeMs, Long responseTimeoutMs, Long eventTimeMs,
                                                   Long lastWriteTimeMs, Long lastReadTimeMs) {
        IotResponseTimeoutEvent result = new IotResponseTimeoutEvent();
        result.afterWriteTimeMs = afterWriteTimeMs;
        result.responseTimeoutMs = responseTimeoutMs;
        result.lastWriteTimeMs = lastWriteTimeMs;
        result.lastReadTimeMs = lastReadTimeMs;
        return result;
    }
}

