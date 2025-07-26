package com.enn.iot.dtu.integration.cit.dto;

import lombok.Data;

@Data
public class ResGatewaySerialNumDTO {
    private String code;
    private String message;
    private GatewayNameDTO data;

    @Data
    public static class GatewayNameDTO {
        private Long id;
        private String name;
        private String serialNum;
    }
}
