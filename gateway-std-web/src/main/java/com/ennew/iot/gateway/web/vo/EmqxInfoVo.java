package com.ennew.iot.gateway.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmqxInfoVo {

    String host;
    int port;
    boolean TLS;
    String userName;
    String password;

}