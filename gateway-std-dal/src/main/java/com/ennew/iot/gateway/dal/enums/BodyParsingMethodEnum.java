package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: body解析方式
 * @Author: qinkun
 * @Date: 2021/11/1 15:21
 */
@Getter
@AllArgsConstructor
public enum BodyParsingMethodEnum {

    NO("无", 1),

    JSON("jsonPath", 2),

    GROOVY("groovy脚本", 3),

    PROTOCOL("协议解析", 4);

    @Getter
    final String name;
    final int code;

    public Integer getCode() {
        return code;
    }

}
