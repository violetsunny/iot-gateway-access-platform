package com.enn.iot.dtu.protocol.api.codec.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public abstract class AbstractIotCmdReq implements Cloneable {
    /**
     * 通讯协议，communication protocol
     */
    protected String commcPrcl;
    /**
     * 设备通讯地址
     */
    protected String commcAddr;

    /**
     * 最大组帧长度
     */
    protected int framingLength;

    /**
     * 延迟防御 1 开 0 关 默认: 开
     */
    protected Integer delayDefensive;

    /**
     * 网关标识
     */
    private String gatewaySn;

    /**
     * 所属设备信息
     */
    private String stationId;
    private String trdPtyCode;

    /**
     * 已经重试次数
     */
    private int hasRetryCount = 1;

    /**
     * true,为读指令；否则，为写指令。
     */
    private Boolean readonly = true;

    /**
     * 数据合规性检查
     *
     * @return Map<字段，错误信息>, if map.isEmpty, 则没有错误。
     */
    public Map<String, String> validate() {
        Map<String, String> errorMap = new HashMap<>(0);
        if (StringUtils.isBlank(gatewaySn)) {
            appendErrorMessage(errorMap, "gatewaySn", "不能为空或null");
        }
        if (StringUtils.isBlank(commcPrcl)) {
            appendErrorMessage(errorMap, "commcPrcl", "不能为空或null");
        }
        if (StringUtils.isBlank(commcAddr)) {
            appendErrorMessage(errorMap, "commcAddr", "不能为空或null");
        }
        if (readonly == null) {
            appendErrorMessage(errorMap, "readonly", "不能为null");
        }
        return errorMap;
    }

    protected void appendErrorMessage(Map<String, String> errorMap, String property, String errorMsg) {
        errorMap.merge(property, errorMsg, (originalMsg, appendMsg) -> originalMsg + "," + appendMsg);
    }

    @Override
    public AbstractIotCmdReq clone() {
        try {
            return (AbstractIotCmdReq)super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("clone异常", e);
            return null;
        }
    }
}

