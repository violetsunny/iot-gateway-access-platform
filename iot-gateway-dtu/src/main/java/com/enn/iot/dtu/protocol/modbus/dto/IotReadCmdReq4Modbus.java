package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant;
import com.enn.iot.dtu.protocol.modbus.utils.IotByteUtils;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;

/**
 * 读指令请求对象
 *
 * @author lixiang
 * @date 2021/11/10
 **/
@Data
@ToString
public class IotReadCmdReq4Modbus extends AbstractIotCmdReq {
    private static final int ADDRESS_INCREMENT_03_04 = 1;
    private static final int ADDRESS_INCREMENT_01_02 = 8;
    /**
     * 功能码
     */
    private Integer functionCode;
    /**
     * 寄存器开始地址，从0开始
     */
    private Integer registerStartAddress;
    /**
     * 读取寄存器个数
     */
    private Integer registerNumber;
    /**
     * 额外向前读取寄存器个数，默认0
     */
    private Integer extraRegisterNumberBefore = 0;
    /**
     * 额外向后读取寄存器个数，默认0
     */
    private Integer extraRegisterNumberAfter = 0;

    /**
     * 下发指令使用：字节数
     */
    private Integer byteNumber;

    /**
     * 下发指令使用：下发的寄存器值
     */
    private Integer addressWriteValue;

    /**
     * 测点配置列表
     */
    private List<IotCmdReqPoint4Modbus> pointList;

    @Override
    public Map<String, String> validate() {
        Map<String, String> errorMap = super.validate();

        try {
            int commcAddrValue = Integer.parseInt(this.commcAddr);
            if (commcAddrValue < 1 || commcAddrValue > 247) {
                appendErrorMessage(errorMap, "commcAddr", "超出有效范围[1,247]");
            }
        } catch (NumberFormatException e) {
            appendErrorMessage(errorMap, "commcAddr", "不是有效数值");
        }

        if (functionCode == null) {
            appendErrorMessage(errorMap, "functionCode", "不能为null");
        }
        if (functionCode < ModbusConstant.FUN_CODE_READ_01 || functionCode > ModbusConstant.FUN_CODE_READ_04) {
            appendErrorMessage(errorMap, "functionCode", "不支持该功能码,目前仅支持0x01、0x02、0x03、0x04功能码");
        }

        if ((functionCode == FUN_CODE_READ_01) || (functionCode == FUN_CODE_READ_02)) {
            // 1 至 2000(0x7D0)
            if (registerNumber < 1 || registerNumber > 2000) {
                appendErrorMessage(errorMap, "registerNumber", "超出有效范围[1,2000]");
            }
        } else if ((functionCode == FUN_CODE_READ_03) || (functionCode == FUN_CODE_READ_04)) {
            // 1 至 125(0x7D)
            if (registerNumber < 1 || registerNumber > 125) {
                appendErrorMessage(errorMap, "registerNumber", "超出有效范围[1,125]");
            }
        }

        if (registerStartAddress == null) {
            appendErrorMessage(errorMap, "registerStartAddress", "不能为null");
        }
        if (extraRegisterNumberBefore == null) {
            appendErrorMessage(errorMap, "extraRegisterNumberBefore", "不能为null");
        }
        if (extraRegisterNumberAfter == null) {
            appendErrorMessage(errorMap, "extraRegisterNumberAfter", "不能为null");
        }
        if (registerNumber == null) {
            appendErrorMessage(errorMap, "registerNumber", "不能为null");
        }

        if (pointList == null) {
            appendErrorMessage(errorMap, "pointList", "不能为null");
        }

        if (registerStartAddress != null) {
            if ((registerStartAddress < 0) || (registerStartAddress > 65535)) {
                appendErrorMessage(errorMap, "registerStartAddress",
                    "寄存器开始地址不合法，值为：" + registerStartAddress + "，超出有效范围[0,65535]");
            }

            int endRegisterStartAddress = calculateRequestRegisterStartAddress() + calculateRequestRegisterNumber() - 1;
            if (endRegisterStartAddress > 65535) {
                appendErrorMessage(errorMap, "endRegisterAddress",
                    "将要读取的寄存器截止地址超过最大数据地址，值为：" + endRegisterStartAddress + "，有效范围[0,65535]");
            }
        }

        if (pointList != null && pointList.size() > 0) {
            for (int i = 0, len = pointList.size(); i < len; i++) {
                final int index = i;
                Map<String, String> pointErrorMap = pointList.get(index).validate();
                pointErrorMap.forEach((property, errorMsg) -> appendErrorMessage(errorMap,
                    "pointList[" + index + "]." + property, errorMsg));
            }
        }
        return errorMap;
    }

    public int calculateResponseBytesNumber() {
        int registerNum = this.extraRegisterNumberBefore + this.registerNumber + this.extraRegisterNumberAfter;
        // 01,02场景，字节长度 = 状态数量/8 + (状态数量%8>0?1:0)
        if (ModbusConstant.FUN_CODE_READ_01 == this.functionCode
            || ModbusConstant.FUN_CODE_READ_02 == this.functionCode) {
            return IotByteUtils.calculateBytesNumberForStatusRegion(calculateRequestRegisterNumber());
        }
        // 03,04场景，字节长度 = 寄存器数量 * 2
        else if (ModbusConstant.FUN_CODE_READ_03 == this.functionCode
            || ModbusConstant.FUN_CODE_READ_04 == this.functionCode) {
            return registerNum * 2;
        } else {
            throw new IllegalArgumentException("不支持的功能码! functionCode:" + this.functionCode);
        }
    }

    /**
     * modbus 通讯时，寄存器开始地址从0开始
     */
    public int calculateRequestRegisterStartAddress() {
        return registerStartAddress - extraRegisterNumberBefore;
    }

    /**
     * 最终的长度：额外向前读取寄存器个数 + 读取寄存器个数 + 额外向后读取寄存器个数
     */
    public int calculateRequestRegisterNumber() {
        if (registerNumber == null) {
            registerNumber = 0;
        }
        if (extraRegisterNumberBefore == null) {
            extraRegisterNumberBefore = 0;
        }
        if (extraRegisterNumberAfter == null) {
            extraRegisterNumberAfter = 0;
        }
        return this.extraRegisterNumberBefore + this.registerNumber + this.extraRegisterNumberAfter;
    }

    public void changeRequestRegisterNumberIf(Integer targetRequestRegisterNumber) {
        int requestRegisterNumber = calculateRequestRegisterNumber();
        // 判断当前请求报文和上一次请求报文长度是否相同
        if (Objects.equals(requestRegisterNumber, targetRequestRegisterNumber)) {
            // 判断起始寄存器地址+寄存器数量是否超过65535？
            // 最后一个的地址：（起始寄存器地址+寄存器数量）- 1
            // (registerStartAddress - 1) 代表：modbus规定配置的时候下标从1开始，但是实际发送报文的时候按照下标为0开始，所以减1；
            int addressIncrement;
            // 01,02场景，字节长度 = 状态数量/8 + (状态数量%8>0?1:0)
            if (ModbusConstant.FUN_CODE_READ_01 == this.functionCode
                || ModbusConstant.FUN_CODE_READ_02 == this.functionCode) {
                addressIncrement = ADDRESS_INCREMENT_01_02;
            }
            // 03,04场景，字节长度 = 寄存器数量 * 2
            else if (ModbusConstant.FUN_CODE_READ_03 == this.functionCode
                || ModbusConstant.FUN_CODE_READ_04 == this.functionCode) {
                addressIncrement = ADDRESS_INCREMENT_03_04;
            } else {
                throw new IllegalArgumentException("不支持的功能码! functionCode:" + this.functionCode);
            }
            int endAddressValue = (registerStartAddress + registerNumber - 1) + addressIncrement;
            if (endAddressValue <= 65535) {
                // 如果小的话：就正常增加1个寄存器数量；
                extraRegisterNumberAfter = addressIncrement;
            } else {
                // 如果大的话：如果读取寄存器地址是65535，读取1个寄存器数量，则往前加1个寄存器数量，读取寄存器地址-1
                extraRegisterNumberBefore = addressIncrement;
            }
        }
    }

    public int calculateResponseDataStartIndex() {
        return extraRegisterNumberBefore * 2;
    }

    public void resetExtra() {
        extraRegisterNumberBefore = 0;
        extraRegisterNumberAfter = 0;
    }

    @Override
    public IotReadCmdReq4Modbus clone() {
        IotReadCmdReq4Modbus newInstance = (IotReadCmdReq4Modbus)super.clone();
        List<IotCmdReqPoint4Modbus> newPointList = new LinkedList<>();
        for (IotCmdReqPoint4Modbus point : this.pointList) {
            newPointList.add(point.clone());
        }
        newInstance.setPointList(newPointList);
        return newInstance;
    }
}
