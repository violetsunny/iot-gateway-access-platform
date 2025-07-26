package com.enn.iot.dtu.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IotDecodeErrorEvent extends AbstractIotEvent {
    private String gatewaySn;

    public IotDecodeErrorEvent() {
        super();
        this.type = "DecodeError";
    }

    public static IotDecodeErrorEvent error(String gatewaySn, String errorCode, String message) {
        IotDecodeErrorEvent event = new IotDecodeErrorEvent();
        event.setGatewaySn(gatewaySn);
        event.setCode(errorCode);
        event.setMessage(message);
        return event;
    }
}
