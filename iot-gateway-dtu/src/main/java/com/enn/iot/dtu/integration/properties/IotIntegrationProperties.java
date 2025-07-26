package com.enn.iot.dtu.integration.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "enn.integration")
@Slf4j
@Data
public class IotIntegrationProperties {
    /**
     * bcs 服务地址
     */
    String bcsBaseUrl;
    /**
     * cit 接口访问凭证
     */
    String bcsToken;
    /**
     * cit 服务地址
     */
    String citBaseUrl;
    /**
     * cit 接口访问凭证
     */
    String citToken;

    @PostConstruct
    private void defaultValue() {
//        if (bcsBaseUrl == null || StringUtils.isBlank(bcsBaseUrl)) {
//            throw new IllegalArgumentException("enn.integration.bcs-base-url 不能为空");
//        }
//        if (bcsToken == null || StringUtils.isBlank(bcsToken)) {
//            throw new IllegalArgumentException("enn.integration.bcs-token 不能为空");
//        }
//        if (citBaseUrl == null || StringUtils.isBlank(citBaseUrl)) {
//            throw new IllegalArgumentException("enn.integration.cit-base-url 不能为空");
//        }
//        if (citToken == null || StringUtils.isBlank(citToken)) {
//            throw new IllegalArgumentException("enn.integration.cit-token 不能为空");
//        }
//        if (log.isInfoEnabled()) {
//            log.info(this.toString());
//        }
    }
}
