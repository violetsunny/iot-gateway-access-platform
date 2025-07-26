/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.ctwing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kanglele
 * @version $Id: CtwingMessage, v 0.1 2023/11/20 15:32 kanglele Exp $
 */
@NoArgsConstructor
@Data
public class CtwingMessage {

    /**
     * upPacketSN
     */
    private Integer upPacketSN;
    /**
     * upDataSN
     */
    private Integer upDataSN;
    /**
     * topic
     */
    private String topic;
    /**
     * timestamp
     */
    private Long timestamp;
    /**
     * tenantId
     */
    private String tenantId;
    /**
     * serviceId
     */
    private String serviceId;
    /**
     * protocol
     */
    private String protocol;
    /**
     * productId
     */
    private String productId;
    /**
     * payload
     */
    private PayloadDTO payload;
    /**
     * messageType=dataReport,deviceOnlineOfflineReport
     */
    private String messageType;
    /**
     * deviceType
     */
    private String deviceType;
    /**
     * deviceId
     */
    private String deviceId;
    /**
     * assocAssetId
     */
    private String assocAssetId;
    /**
     * imsi
     */
    @JsonProperty("IMSI")
    private String IMSI;

    private void setImsi(String imsi) {
        if (imsi != null && "".equals(imsi.trim())) {
            this.IMSI = imsi;
        }
    }

    /**
     * imei
     */
    @JsonProperty("IMEI")
    private String IMEI;

    private void setImei(String imei) {
        if (imei != null && "".equals(imei.trim())) {
            this.IMEI = imei;
        }
    }

    /**
     * PayloadDTO
     */
    @NoArgsConstructor
    @Data
    public static class PayloadDTO {
        /**
         * aPPdata
         */
        @JsonProperty("APPdata")
        private String aPPdata;
    }
}
