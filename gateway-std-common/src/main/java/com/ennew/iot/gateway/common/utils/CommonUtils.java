package com.ennew.iot.gateway.common.utils;

import java.util.UUID;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 12:40:35
 */
public class CommonUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}
