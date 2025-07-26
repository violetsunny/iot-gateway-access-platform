package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NetworkConfigState implements EnumType {

    enabled(1, "已启动"),
    paused(0, "已暂停"),
    disabled(-1, "已停止");

    private final Integer code;
    private final String text;

    public static NetworkConfigState convert(String value) {
        return EnumUtil.getValue(NetworkConfigState.class, value);
    }

    @Override
    public String getName() {
        return name();
    }

}

