/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kanglele
 * @version $Id: NetworkEnum, v 0.1 2023/11/15 15:29 kanglele Exp $
 */
@AllArgsConstructor
@Getter
public enum NetworkEnum {

    MQTT("MQTT", "MQTT"),
    HTTP("HTTP", "HTTP"),
    TCP("TCP", "TCP"),
    MODUBUS("MODUBUS", "MODUBUS"),
    CTWING("CTWING", "电信AEP"),
    ;

    private final String code;
    private final String desc;

}
