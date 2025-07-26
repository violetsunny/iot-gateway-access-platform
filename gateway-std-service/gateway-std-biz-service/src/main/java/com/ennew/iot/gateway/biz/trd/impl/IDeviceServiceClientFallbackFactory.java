package com.ennew.iot.gateway.biz.trd.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: qinkun
 * @Date: 2024/04/16 15:28
 */
@Slf4j
@Component
public class IDeviceServiceClientFallbackFactory implements FallbackFactory<IDeviceServiceClientFallback> {

    @Override
    public IDeviceServiceClientFallback create(Throwable cause) {
        log.error("", cause);
        return new IDeviceServiceClientFallback();
    }

}
