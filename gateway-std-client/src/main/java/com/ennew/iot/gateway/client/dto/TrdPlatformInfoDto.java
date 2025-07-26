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
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: TrdPlatformBasic, v 0.1 2024/3/12 17:30 kanglele Exp $
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrdPlatformInfoDto {

    private Long id;

    private Integer pType;

    private String pCode;

    private String pName;

    private String pSource;

    private Map<String, String> configJson;

    /**
     * 协议Id
     */
    private String protocolId;

    private String createUser;

    private String updateUser;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private String remark;

}
