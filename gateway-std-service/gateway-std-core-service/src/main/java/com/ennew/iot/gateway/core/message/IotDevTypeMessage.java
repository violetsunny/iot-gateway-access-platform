package com.ennew.iot.gateway.core.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class IotDevTypeMessage {

    @JsonProperty("version")
    private String version;
    @JsonProperty("devId")
    private String devId;
    @JsonProperty("deviceName")
    private String deviceName;
    @JsonProperty("devType")
    private String devType;
    @JsonProperty("period")
    private String period;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("sn")
    private String sn;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("deptId")
    private String deptId;
    @JsonProperty("ts")
    private Long ts;
    /**
     *  0-否，1-是
     */
    @JsonProperty("debug")
    private Integer debug;
    /**
     * N-否，Y-是
     */
    @JsonProperty("resume")
    private String resume;
    /**
     * 2688/cim/custom
     */
    @JsonProperty("source")
    private String source;
    @JsonProperty("domain")
    private String domain;
    /**
     * 第三方设备标识
     */
    @JsonProperty("deviceCode")
    private String deviceCode;
    @JsonProperty("staId")
    private String staId;
    /**
     * 上游时间，非kafka消息取当前时间
     */
    @JsonProperty("ingestionTime")
    private Long ingestionTime;
    @JsonProperty("entityTypeName")
    private String entityTypeName;
    @JsonProperty("uploadFrequency")
    private String uploadFrequency;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("county")
    private String county;
    @JsonProperty("deviceType")
    private String deviceType;
    @JsonProperty("parentId")
    private String parentId;
    @JsonProperty("data")
    private List<Metrics> data;

    @NoArgsConstructor
    @Data
    public static class Metrics {
        @JsonProperty("metric")
        private String metric;
        @JsonProperty("value")
        private Object value;
        @JsonProperty("metricUnit")
        private String metricUnit;
        @JsonProperty("metricName")
        private String metricName;
        @JsonProperty("max")
        private Object max;
        @JsonProperty("min")
        private Object min;
        @JsonProperty("type")
        private String type;
    }
}
