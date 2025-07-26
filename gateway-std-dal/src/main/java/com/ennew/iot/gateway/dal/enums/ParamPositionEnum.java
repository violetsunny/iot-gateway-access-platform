package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 参数位置
 * @Author: qinkun
 * @Date: 2021/11/18 16:16
 */
@Getter
@AllArgsConstructor
public enum ParamPositionEnum {

    HEAD("Head", 1),

    QUERY("Query", 2),

    PATH("Path", 3),

    FORM("Form表单", 4),

    BODY("Body", 5);

    final String name;

    final int code;

    public Integer getCode() {
        return code;
    }
	
}
