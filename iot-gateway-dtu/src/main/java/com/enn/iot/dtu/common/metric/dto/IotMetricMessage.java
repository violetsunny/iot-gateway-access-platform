package com.enn.iot.dtu.common.metric.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * [
 *   {
 *     "domain": "IOTM",
 *     "v": "1",
 *     "time": 1638777090963,
 *     "tags": {
 *       "systemCode": "PARK01_EMS70",
 *       "appId": "iot-gateway-dtu",
 *       "gatewayId": "CS01T_001"
 *     },
 *     "metrics": {
 *       "gateway.s.online": 1,
 *       "connected.time": 1638776952853,
 *       "online.status": 1
 *     }
 *   }
 * ]
 * </code>
 **/
@Data
public class IotMetricMessage {
    private String domain = "IOTM";
    private String v = "1";
    private Long time;
    @Setter
    @Getter
    private IotMetricTags tags = new IotMetricTags();
    private Map<String, Number> metrics = new HashMap<>(5);

    public void setOnlineStatus(boolean online) {
        metrics.put("online.status", online ? 1 : 0);
        metrics.put("gateway.s.online", online ? 1 : 0);
    }

    public void setConnectedTime(Long timeSecond) {
        metrics.put("connected.time", timeSecond);
    }

    @Data
    public static class IotMetricTags {
        private String systemCode;
        private String appId;
        private String gatewayId;
        private String address;
    }

}
