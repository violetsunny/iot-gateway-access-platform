package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "NetworkConfigResVo对象")
@Data
public class NetworkConfigResVo {

    private String id;

    private String name;

    private String type;

    private Map<String,Object> configuration;

    private String description;
}
