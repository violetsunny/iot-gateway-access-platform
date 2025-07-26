package com.ennew.iot.gateway.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: alec
 * Description:
 * @date: 下午3:35 2023/5/25
 */
@Getter
@AllArgsConstructor
public enum CloudDockingType {

    /**
     * 请求认证参数的类型
     * */

    AUTH("Auth"),
    PULL_DATA("PullData"),
    SEND_CMD("SendCmd"),

    ;

    private final String code;


    @Getter
    @AllArgsConstructor
    public enum RequestType {

        FORM("Form"),
        JSON("Json"),
        ;
        private final String code;
    }
}
