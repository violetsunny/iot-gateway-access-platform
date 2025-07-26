/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author kanglele
 * @version $Id: TrdPlatformBasic, v 0.1 2024/3/12 17:30 kanglele Exp $
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrdPlatformTaskMessage {

    private Long id;

    private String pCode;

    private String taskCode;

    private String taskName;

    private Long apiId;

    private String frequency;

    private String productId;

    private Integer status;

    private Integer operate;
}
