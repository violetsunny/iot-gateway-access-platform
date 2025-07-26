package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
public class CloudGatewayModbusPointValidateBO {


    private String productId;


    private String entityTypeCode;


    private String entityCode;


    public boolean productEqual(String importProductId){
        return Objects.equals(this.productId, importProductId);
    }


    public boolean metricExits(String importDeviceMetric, Map<String, Set<String>> entityMetricMap){
        Set<String> metrics = entityMetricMap.get(this.entityCode);
        if(metrics == null){
            return false;
        }
        return metrics.contains(importDeviceMetric);
    }
}
