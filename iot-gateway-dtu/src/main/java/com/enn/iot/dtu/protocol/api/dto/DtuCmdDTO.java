package com.enn.iot.dtu.protocol.api.dto;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DtuCmdDTO
 * @Description
 * @Author nixiaolin
 * @Date 2022/2/16 13:46
 **/

@Slf4j
@Data
@ToString
public class DtuCmdDTO implements Cloneable {
    private String stationId;
    private String trdPtyCode;
    private String metric;
    private Long seq;
    /**
     * 指令下发的时间戳
     */
    private Long ts;
    private Number value;

    @Override
    public DtuCmdDTO clone() {
        try {
            return (DtuCmdDTO)super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("clone异常", e);
            return null;
        }
    }
}
