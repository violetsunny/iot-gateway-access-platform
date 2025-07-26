package com.enn.iot.dtu.protocol.api.codec.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class IotCmdRespPoint {
    private String pointCode;
    private String deviceTrdPtyCode;
    private String systemAliasCode;
    /**
     * 单位：毫秒
     */
    private Long timeMs;
    private Number value;
}
