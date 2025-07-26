package com.enn.iot.dtu.common.outer.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class IotOuterEventTags {
    /**
     * 事件类型编码
     */
    @Setter(AccessLevel.NONE)
    public String event;
    /**
     * 网关标识
     */
    public String gatewaySn;
    /**
     * 事件类型
     */
    public String channelId;

    public void setEvent(IotOuterEventEnum iotOuterEventEnum) {
        this.event = iotOuterEventEnum.getCode();
    }

}
