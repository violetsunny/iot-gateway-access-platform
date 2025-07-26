package com.enn.iot.dtu.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IotClearDecodeCumulatorEvent extends AbstractIotEvent {


    public IotClearDecodeCumulatorEvent() {
        super();
        this.type = "ClearDecodeCumulator";
    }

    public static IotClearDecodeCumulatorEvent instance() {
        return new IotClearDecodeCumulatorEvent();
    }
}
