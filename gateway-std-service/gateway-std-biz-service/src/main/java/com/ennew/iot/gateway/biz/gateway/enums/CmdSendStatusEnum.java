package com.ennew.iot.gateway.biz.gateway.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CmdSendStatusEnum{
    accepted(0,"已接收"),
    processing(-1,"处理中"),
    sent(1,"已发送");
    int code;
    String name;
    CmdSendStatusEnum(int code,String name){
        this.code= code;
        this.name=name;
    }
    public static String getStatusName(int code){
        return Arrays.stream(values()).filter(status -> status.code == code).findFirst().orElse(null).getName();
    }
}
