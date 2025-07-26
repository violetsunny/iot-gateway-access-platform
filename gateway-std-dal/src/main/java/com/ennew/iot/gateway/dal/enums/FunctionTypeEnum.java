/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kanglele
 * @version $Id: FunctionTypeEnum, v 0.1 2024/3/15 10:37 kanglele Exp $
 */
@Getter
@AllArgsConstructor
public enum FunctionTypeEnum {

    UP("上数", 1),

    DOWN("下控", 2),
    ;

    final String name;
    final int code;

}
