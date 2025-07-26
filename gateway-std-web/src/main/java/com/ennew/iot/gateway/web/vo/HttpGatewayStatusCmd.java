package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @Author: lyz
 * Description: http 设备工况上报
 * @date: 上午9:40 2023/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HttpGatewayStatusCmd {
    private String ver;
    @JsonProperty(value = "pKey")
    private String pKey;
    @NotNull(message = "sn is not null")
    private String sn;
    private String type;
    private Long seq;
    @NotNull(message = "ts is not null")
    private Long ts;

    @NotEmpty(message = "data is not null")
    private Map<String,Object> data;
}
