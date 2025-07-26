package com.enn.iot.dtu.protocol.api.codec.dto;

import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public abstract class AbstractIotCmdReqPoint implements Cloneable {
    /**
     * 通讯协议，communication protocol </br>
     * 冗余字段，参数检验依赖此字段
     */
    protected String commcPrcl;
    protected String pointCode;
    protected String deviceTrdPtyCode;
    protected String systemAliasCode;

    /**
     * 数据合规性检查
     *
     * @return Map<字段，错误信息>, if map.isEmpty, 则没有错误。
     */
    public Map<String, String> validate() {
        Map<String, String> errorMap = new HashMap<>(0);
        if (StringUtil.isNullOrEmpty(pointCode)) {
            appendErrorMessage(errorMap, "pointCode", "不能为空或null");
        }
        if (StringUtil.isNullOrEmpty(deviceTrdPtyCode)) {
            appendErrorMessage(errorMap, "trdPtyCode", "不能为空或null");
        }
        if (StringUtil.isNullOrEmpty(systemAliasCode)) {
            appendErrorMessage(errorMap, "systemAliasCode", "不能为空或null");
        }
        return errorMap;
    }

    protected void appendErrorMessage(Map<String, String> errorMap, String property, String errorMsg) {
        errorMap.merge(property, errorMsg, (originalMsg, appendMsg) -> originalMsg + "," + appendMsg);
    }

    @Override
    protected AbstractIotCmdReqPoint clone() {
        try {
            return (AbstractIotCmdReqPoint)super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("clone异常", e);
            return null;
        }
    }
}
