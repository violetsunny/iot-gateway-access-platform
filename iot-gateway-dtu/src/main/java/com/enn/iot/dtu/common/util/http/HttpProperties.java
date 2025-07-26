package com.enn.iot.dtu.common.util.http;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = HttpProperties.PROPERTIES_KEY_PREFIX)
@Slf4j
@Data
public class HttpProperties {
    public static final String PROPERTIES_KEY_PREFIX = "enn.http";
    public static final String PROPERTIES_KEY_ENABLED = PROPERTIES_KEY_PREFIX + ".enabled";
    private Boolean enabled;
    private Integer maxIdleConnections;
    /**
     * TimeUnit.SECONDS
     */
    private Integer keepAliveDuration;
    /**
     * TimeUnit.SECONDS
     */
    private Integer connectTimeout;
    /**
     * TimeUnit.SECONDS
     */
    private Integer readTimeout;
    private Boolean requestLogEnabled;
    private Boolean responseLogEnabled;

    @PostConstruct
    private void postConstruct() {
        if (enabled == null) {
            enabled = false;
        }
        if (maxIdleConnections == null) {
            maxIdleConnections = 20;
        }
        if (keepAliveDuration == null) {
            keepAliveDuration = 59;
        }
        if (connectTimeout == null) {
            connectTimeout = 3;
        }
        if (readTimeout == null) {
            readTimeout = 20;
        }
        if (requestLogEnabled == null) {
            requestLogEnabled = false;
        }
        if (responseLogEnabled == null) {
            responseLogEnabled = false;
        }
        if (enabled) {
            if (log.isInfoEnabled()) {
                log.info(this.toString());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("HttpTemplate is not enabled. You can set properties with \"{}\" to enable.",
                        PROPERTIES_KEY_ENABLED);
            }
        }
    }
}
