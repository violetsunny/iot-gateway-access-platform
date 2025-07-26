package com.enn.iot.dtu.common.msg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author shq
 * @date 2019/04/19
 */
@Data
@ToString
public class IotMqttMetric {

    @JsonProperty("m")
    private String pointCode;

    @JsonProperty("v")
    private Number value;

    @JsonProperty("ts")
    private Long timestamp;

    @JsonProperty("dq")
    private Integer dataQuality = 0;
}
