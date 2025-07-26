package com.enn.iot.dtu.common.msg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author shq
 * @date 2019/04/19
 */
@Data
@ToString
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
