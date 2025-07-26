package com.ennew.iot.gateway.biz.trd.impl;

import com.ennew.iot.gateway.biz.trd.IDeviceServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description:
 * @Author: qinkun
 * @Date: 2024/04/16 15:28
 */
@Slf4j
@Component
public class IDeviceServiceClientFallback implements IDeviceServiceClient {

    @Override
    public String getInfoByCode(String entityTypeCode, String source) {
        return null;
    }

    @Override
    public String getMeasureInfoByEntityTypeId(String entityTypeId) {
        return null;
    }

    @Override
    public String getProductInfoByEntityTypeCode(String entityTypeCode, Map<String, Object> body) {
        return null;
    }

    @Override
    public String getDefaultProductId(String entityTypeId) {
        return null;
    }

    @Override
    public String getProduct(String productId) {
        return null;
    }

}
