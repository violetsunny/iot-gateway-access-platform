package com.enn.iot.dtu.common.devicecmdstatus.dto;

import lombok.Data;

@Data
public class DeviceCmdHealthStatus {
    /**
     * 子设备系统编码
     */
    private String stationId;

    /**
     * 子设备三方标识
     */
    private String trdPtyCode;

    /**
     * deviceHealth =true 子设备通信正常,该设备下的所有采集指令都执行完成后至少有一个指令是解析成功的；
     * deviceHealth =false 子设备通信异常；
     */
    private boolean deviceHealth = true;

    /**
     * hasCmdHealth=true  该设备下有指令执行成功
     * hasCmdHealth=false 该设备所有指令都执行失败
     */
    private boolean hasCmdHealth = false;
}
