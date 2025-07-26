package com.enn.iot.dtu.common.outer.event;

import lombok.Data;

@Data
public class IotOuterEventFields {
    /**
     * 连接时间
     */
    public Number connectedAt;
    /**
     * 断开连接时间
     */
    public Number disconnectedAt;

}
