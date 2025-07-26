package com.enn.iot.dtu.common.outer.msg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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
