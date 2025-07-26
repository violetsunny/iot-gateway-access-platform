package com.enn.iot.dtu.common.outer.msg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class KafkaPropertyMessage {

    private String version;
    private String devId;
    private String deviceName;
    private String devType;
    private String period;
    private String productId;
    private String sn;
    private String tenantId;
    private String deptId;
    private Long ts;
    /**
     *  0-否，1-是
     */
    private Integer debug;
    /**
     * N-否，Y-是
     */
    private String resume;
    /**
     * 2688/cim/custom
     */
    private String source;
    private List<Metrics> data;

    @NoArgsConstructor
    @Data
    public static class Metrics {
        private String metric;
        private Object value;
    }
}
