package com.ennew.iot.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolWayEnum {

    UP("上行", 1),

    DOWN("下行", 2);

    final String name;
    final Integer code;

    ProtocolWayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Integer getCode(String name) {
        for (ProtocolWayEnum e : ProtocolWayEnum.values()) {
            if (e.getName().equals(name)) {
                return e.getCode();
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (ProtocolWayEnum e : ProtocolWayEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }

}
