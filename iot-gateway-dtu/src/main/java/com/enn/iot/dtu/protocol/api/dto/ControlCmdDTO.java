package com.enn.iot.dtu.protocol.api.dto;

import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DtuDTO
 * @Description
 * @Author nixiaolin
 * @Date 2021/11/1 14:24
 **/
@Slf4j
@Data
@ToString
@EqualsAndHashCode()
public class ControlCmdDTO {
    private String gatewaySn;
    private String commcAddr;
    private String commcPrcl;
    private DtuCmdDTO cmdDTO;
    /**
     * DTU 云网关接受指令的时间戳
     */
    private Long receiveTs;
    private DtuDeviceDTO deviceDTO;

    public Map<String, String> validate(DtuCmdDTO dto) {
        Map<String, String> errorMap = new HashMap<>(0);
        if (StringUtil.isNullOrEmpty(dto.getMetric())) {
            appendErrorMessage(errorMap, "metric", "不能为空或null");
        }
        if (StringUtil.isNullOrEmpty(dto.getStationId())) {
            appendErrorMessage(errorMap, "stationId", "不能为空或null");
        }
        if (StringUtil.isNullOrEmpty(dto.getTrdPtyCode())) {
            appendErrorMessage(errorMap, "trdPtyCode", "不能为空或null");
        }
        if (dto.getSeq() == null) {
            appendErrorMessage(errorMap, "seq", "不能为空或null");
        }
        if (dto.getValue() == null) {
            appendErrorMessage(errorMap, "value", "不能为空或null");
        }
        return errorMap;
    }

    protected void appendErrorMessage(Map<String, String> errorMap, String property, String errorMsg) {
        errorMap.merge(property, errorMsg, (originalMsg, appendMsg) -> originalMsg + "," + appendMsg);
    }

}
