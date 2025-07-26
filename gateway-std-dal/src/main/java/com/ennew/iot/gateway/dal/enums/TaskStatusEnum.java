/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kanglele
 * @version $Id: TaskStatuEnum, v 0.1 2024/3/26 10:30 kanglele Exp $
 */
@Getter
@AllArgsConstructor
public enum TaskStatusEnum {

    NO_START("未启动", 1),

    START("启动", 2),

    PAUSE("暂停", 3),
    ;

    final String name;

    final int code;

}
