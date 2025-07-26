/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: HttpEventDataBo, v 0.1 2023/5/12 10:21 kanglele Exp $
 */
@Data
public class HttpEventDataBo {
    private String version;
    private String pKey;
    private String sn;
    private Long ts;
    private List<Devs> devs;

    @NoArgsConstructor
    @Data
    public static class Devs {
        private String sysId;
        private String deviceId;
        private String identifier;
        private Long ts;
        private String eventType;
        private Map<String,Object> value;
    }
}
