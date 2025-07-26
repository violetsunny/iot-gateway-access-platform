/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kanglele
 * @version $Id: ApiTypeEnum, v 0.1 2024/3/15 11:13 kanglele Exp $
 */
@Getter
@AllArgsConstructor
public enum ApiTypeEnum {
    AUTH("认证", 1),

    DATA("数据", 2),

    PAGE("分页", 3);

    final String name;
    final int code;
}
