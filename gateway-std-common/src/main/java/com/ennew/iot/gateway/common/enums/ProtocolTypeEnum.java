package com.ennew.iot.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolTypeEnum {

    JAR("jar", 1),

    SCRIPT("脚本", 2);

    final String name;
    final Integer code;

    ProtocolTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Integer getCode(String name) {
        for (ProtocolTypeEnum e : ProtocolTypeEnum.values()) {
            if (e.getName().equals(name)) {
                return e.getCode();
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (ProtocolTypeEnum e : ProtocolTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }

}
