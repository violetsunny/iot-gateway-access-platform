package com.enn.iot.dtu.protocol.api.maindata.dto;

import lombok.Data;

import java.util.List;

@Data
public class DtuPointInfoDTO {
    /**
     * 网关标识
     */
    String gatewaySerialNum;
    /**
     * 站id
     */
    String stationId;
    /**
     * 第三方标识
     */
    String trdPtyCode;
    /**
     * 通讯地址地址
     */
    String commcAddr;
    /**
     * 通讯规约
     */
    String commcPrcl;
    /**
     * 最大组帧长度
     */
    int framingLength;

    /**
     * 延迟防御 1 开 0 关 默认: 开
     */
    Integer delayDefensive;

    /**
     * 设备主键
     */
    String id;
    /**
     * 测点信息
     */
    List<CimPointDTO> pointInfo;
}
