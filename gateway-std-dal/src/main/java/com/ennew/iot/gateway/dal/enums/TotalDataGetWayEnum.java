package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 总数获取方式
 * @Author: qinkun
 * @Date: 2021/11/1 15:21
 */
@Getter
@AllArgsConstructor
public enum TotalDataGetWayEnum {

    FIXED("固定值", 1),

    ORIGINAL_API("原始接口获取", 2),

    NEW_API("单独接口获取", 3);

    @Getter
    final String name;
    final int code;

    public Integer getCode() {
        return code;
    }

}
