/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.protocol;

import com.ennew.iot.gateway.biz.protocol.cloud.CloudProtocolSupportProvider;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author kanglele
 * @version $Id: CloudProtocolSupports, v 0.1 2023/5/22 17:45 kanglele Exp $
 */
@Configuration
@Component
public class ProtocolSupportConfig {

    @Bean
    public ProtocolSupport cloudProtocol(SpringContextUtil serviceContext) {
        return new CloudProtocolSupportProvider().create(serviceContext);
    }
}
