package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ProtocolReplicaBo implements Serializable {

    //协议id
    private String id;

    private Integer way;

    //协议配置
    private Map<String, Object> configuration;

}
