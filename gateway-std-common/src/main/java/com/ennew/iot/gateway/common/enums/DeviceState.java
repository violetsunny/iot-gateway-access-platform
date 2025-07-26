package com.ennew.iot.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeviceState {
    notActive("未激活"),
    offline("离线"),
    online("在线");

    private final String text;

    public String getValue() {
        return name();
    }

    public static DeviceState of(int state) {
        switch (state) {
            case -1:
                return offline;
            case 1:
                return online;
            default:
                return notActive;
        }
    }

    public static Integer of(DeviceState state) {
        switch (state) {
            case offline:
                return -1;
            case online:
                return 1;
            default:
                return -3;
        }
    }
}
