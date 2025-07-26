package com.enn.iot.dtu.protocol.api.maindata.dto;

import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class MainDataDTO {
    private String gatewaySn;
    private String stationId;
    private List<DtuDeviceDTO> deviceList;
    /**
     * 主数据中档案的修改时间
     */
    private Long updateTime;

    public static MainDataDTO newInstance(String gatewaySn) {
        MainDataDTO result = new MainDataDTO();
        result.setGatewaySn(gatewaySn);
        return result;
    }

    public List<DtuDeviceDTO> filter(MainDataDTO mainDataDTO) {
        List<DtuDeviceDTO> list = new ArrayList<>();
        for(DtuDeviceDTO deviceDTO : mainDataDTO.getDeviceList()) {
            if(null == deviceDTO) {
                if(log.isWarnEnabled()) {
                    log.warn("设备信息为空，将跳过该设备，网关序列号:{}", gatewaySn);
                }
                continue;
            }

            if(StringUtil.isNullOrEmpty(deviceDTO.getTrdPtyCode())) {
                if(log.isWarnEnabled()) {
                    log.warn("设备三方标识为空，将跳过该设备，网关序列号:{}，", gatewaySn);
                }
                continue;
            }

            if(StringUtil.isNullOrEmpty(deviceDTO.getCommcPrcl())) {
                deviceDTO.setCommcPrcl(ProtocolTypeEnum.MODBUS_RTU.getValue());
                if(log.isWarnEnabled()) {
                    log.warn("设备通信协议为空，将设置该设备的通信协议为默认值为MODBUS_RTU，网关序列号:{}，设备三方标识：{}", gatewaySn,
                            deviceDTO.getTrdPtyCode());
                }
            }
            list.add(deviceDTO);
        }
        return list;
    }
}
