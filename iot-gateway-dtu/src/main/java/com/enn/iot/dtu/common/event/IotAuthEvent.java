package com.enn.iot.dtu.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IotAuthEvent extends AbstractIotEvent {

    public static final String CODE_AUTH_SUCCESS = "auth.success";
    public static final String CODE_AUTH_FAILED = "auth.failed";

    private boolean success;
    private String gatewaySn;

    public IotAuthEvent() {
        super();
        this.type = "Auth";
    }

    public static IotAuthEvent success(String message, String gatewaySn) {
        IotAuthEvent event = new IotAuthEvent();
        event.setCode(CODE_AUTH_SUCCESS);
        event.setSuccess(true);
        event.setMessage(message);
        event.setGatewaySn(gatewaySn);
        return event;
    }

    public static IotAuthEvent failed(String message) {
        IotAuthEvent event = new IotAuthEvent();
        event.setCode(CODE_AUTH_SUCCESS);
        event.setSuccess(false);
        event.setMessage(message);
        event.setGatewaySn(null);
        return event;
    }
}
