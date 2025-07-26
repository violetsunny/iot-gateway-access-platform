package com.enn.iot.dtu.common.event;

import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IotDetectionErrorEvent extends AbstractIotEvent {
    private String gatewaySn;
    private IotDecodeCodeEnum detectionCode;

    public IotDetectionErrorEvent() {
        super();
        this.type = "DecodeError";
    }

    public static IotDetectionErrorEvent error(String gatewaySn, IotDecodeCodeEnum detectionCode, String message) {
        IotDetectionErrorEvent event = new IotDetectionErrorEvent();
        event.setCode("error");
        event.setMessage(message);
        event.setDetectionCode(detectionCode);
        event.setGatewaySn(gatewaySn);
        return event;
    }
}
