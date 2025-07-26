package com.ennew.iot.gateway.biz.protocol.enums;

public enum ProtocolDirectionTypeEnum {


    UP(1),
    DOWN(2);


    private final Integer code;

    ProtocolDirectionTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }


}
