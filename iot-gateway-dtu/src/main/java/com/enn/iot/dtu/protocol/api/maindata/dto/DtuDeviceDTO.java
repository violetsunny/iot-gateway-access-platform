package com.enn.iot.dtu.protocol.api.maindata.dto;

import lombok.Data;

import java.util.List;

@Data
public class DtuDeviceDTO {
    private String commcAddr;
    private String commcPrcl;
    private String id;
    private String stationId;
    private String trdPtyCode;
    /**
     * 最大组帧长度
     */
    private int framingLength;

    /**
     * 延迟防御 1 开 0 关 默认: 开
     */
    Integer delayDefensive;
    private List<CimPointDTO> pointInfo;

    private String entityTypeCode;

    private String entityTypeSource;

    private String deviceName;

    private String period;

    private String sn;

    private String productId;

    private String tenantId;

    private String deptId;

    private Integer testFlag;

}
