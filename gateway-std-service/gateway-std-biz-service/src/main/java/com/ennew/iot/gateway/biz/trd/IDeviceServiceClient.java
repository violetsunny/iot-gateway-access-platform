package com.ennew.iot.gateway.biz.trd;

import com.ennew.iot.gateway.biz.trd.impl.IDeviceServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        value = "iot-device-service",
        url = "${ennew.iot.device.url:''}",
        fallback = IDeviceServiceClientFallback.class
)
public interface IDeviceServiceClient {

    @GetMapping("/entityType/getInfoByCode/{entityTypeCode}")
    String getInfoByCode(@PathVariable("entityTypeCode") String entityTypeCode, @RequestParam("source") String source);

    @GetMapping("/entityType/measure/property/list/{entityTypeId}")
    String getMeasureInfoByEntityTypeId(@PathVariable("entityTypeId") String entityTypeId);

    @PostMapping("/product/entityType/{entityTypeCode}")
    String getProductInfoByEntityTypeCode(@PathVariable("entityTypeCode") String entityTypeCode, @RequestBody Map<String, Object> body);

    @GetMapping("/product/get/defaultProductId")
    String getDefaultProductId(@RequestParam("entityTypeId") String entityTypeId);

    @GetMapping("/product/get/byProductId/{productId}")
    String getProduct(@PathVariable("productId") String productId);

}
