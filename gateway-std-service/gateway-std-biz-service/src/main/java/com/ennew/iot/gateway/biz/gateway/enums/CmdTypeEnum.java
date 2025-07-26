package com.ennew.iot.gateway.biz.gateway.enums;

public enum CmdTypeEnum {
    SET("cmd/set"),

    SET_ALL("cmd/setall"),
    EVENT("cmd/event"),
    SERVICE("cmd/service"),

    NTP_SET("ntp/set"),
    INFO("info"),
    STATUS("status"),
    HISTORY("history");
    private String code;

    CmdTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
