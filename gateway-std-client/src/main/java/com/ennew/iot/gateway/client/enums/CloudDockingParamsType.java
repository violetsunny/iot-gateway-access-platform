package com.ennew.iot.gateway.client.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:16 2023/5/25
 */
@Getter
@AllArgsConstructor
public enum CloudDockingParamsType {

    /**
     * 请求认证参数的类型
     * */

    PARAMS("Params"),
    HEADER("Header"),
    PATH("Path"),
    BODY("Body"),

    ;

    private final String code;
}
