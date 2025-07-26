/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author kanglele
 * @version $Id: TrdPlatformBasic, v 0.1 2024/3/12 17:30 kanglele Exp $
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrdPlatformReqDto {

    private Long apiId;

    private String code;

    private String taskCode;

    private String productId;

    private Integer status;

}
