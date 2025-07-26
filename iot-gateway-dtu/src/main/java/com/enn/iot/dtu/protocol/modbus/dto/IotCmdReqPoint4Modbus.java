package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReqPoint;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author lixiang
 * @date 2021/11/10
 **/
@Data
@ToString
public class IotCmdReqPoint4Modbus extends AbstractIotCmdReqPoint {
    /**
     * 功能码，冗余存储，支持单元测试用例
     */
    private Integer functionCode;
    /**
     * 寄存器地址，从0开始
     */
    private Integer registerAddress;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 字节序
     */
    private String byteOrder;

    @Override
    public Map<String, String> validate() {
        Map<String, String> errorMap = super.validate();
        if (functionCode == null) {
            appendErrorMessage(errorMap, "functionCode", "不能为null");
        }
        if (registerAddress == null) {
            appendErrorMessage(errorMap, "registerAddress", "不能为null");
        }
        if (StringUtil.isNullOrEmpty(dataType)) {
            appendErrorMessage(errorMap, "dataType", "不能为空或null");
        }
        if (StringUtil.isNullOrEmpty(byteOrder)) {
            appendErrorMessage(errorMap, "byteOrder", "不能为空或null");
        }
        return errorMap;
    }

    @Override
    public IotCmdReqPoint4Modbus clone() {
        return (IotCmdReqPoint4Modbus)super.clone();
    }
}
