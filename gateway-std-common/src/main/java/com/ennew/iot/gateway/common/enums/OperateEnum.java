/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kanglele
 * @version $Id: OperateEnum, v 0.1 2024/3/22 14:31 kanglele Exp $
 */
@Getter
@AllArgsConstructor
public enum OperateEnum {
    ADD("添加", 1),

    UPDATE("修改", 2),

    REMOVE("删除", 3);

    final String name;
    final int code;
}
