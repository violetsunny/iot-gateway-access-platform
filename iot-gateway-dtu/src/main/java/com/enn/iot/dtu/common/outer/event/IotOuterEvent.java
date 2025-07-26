package com.enn.iot.dtu.common.outer.event;

import lombok.Data;

@Data
public class IotOuterEvent {
    IotOuterEventFields fields;
    IotOuterEventTags tags;
    Long time;

    public IotOuterEvent() {
        this.fields = new IotOuterEventFields();
        this.tags = new IotOuterEventTags();
    }
}
