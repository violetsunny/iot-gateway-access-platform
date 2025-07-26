package com.enn.iot.dtu.common.outer.msg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IotMqttDevice {

    @JsonProperty("sysId")
    private String systemAliasCode;

    @JsonProperty("dev")
    private String deviceTrdPtyCode;

    @JsonProperty("ts")
    private Long timestamp;

    @JsonProperty("d")
    private List<IotMqttMetric> pointList;
}
