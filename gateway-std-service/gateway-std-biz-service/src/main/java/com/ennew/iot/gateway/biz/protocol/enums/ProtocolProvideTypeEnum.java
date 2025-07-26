package com.ennew.iot.gateway.biz.protocol.enums;

public enum ProtocolProvideTypeEnum {


    JAR("jar"),
    SCRIPT("script");


    private final String name;

    ProtocolProvideTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
