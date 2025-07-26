package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: HttpMethod
 * @Author: qinkun
 * @Date: 2021/11/1 15:21
 */
@Getter
@AllArgsConstructor
public enum HttpMethodEnum {

    GET("GET", 1),

    POST("POST", 2);

    @Getter
    final String name;
    final int code;

    public Integer getCode() {
        return code;
    }

    public static String getName(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (HttpMethodEnum e : HttpMethodEnum.values()) {
            if (e.getCode().equals(Integer.parseInt(code))) {
                return e.getName();
            }
        }
        return null;
    }

}
