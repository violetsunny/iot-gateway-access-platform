/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.starter.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * @author kanglele
 * @version $Id: SpringDocSwaggerConfig, v 0.1 2023/2/3 19:15 kanglele Exp $
 */

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
        info = @Info(
                title = "物联网平台接入模块",
                description = "物联网平台接入模块接口文档",
                contact = @Contact(name = "admin",url = "https://admin-iot.ennew.com/"),
                version = "1.1.0"
        )
)
@AutoConfigureBefore(SpringDocConfiguration.class)
//@Data
//@RequiredArgsConstructor
public class SpringDocSwaggerConfig {

}
