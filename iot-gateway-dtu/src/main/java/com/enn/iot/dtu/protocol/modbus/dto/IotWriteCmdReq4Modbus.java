package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.modbus.constant.DataTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.enn.iot.dtu.protocol.modbus.constant.DataTypeEnum.getInstance;
import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;
import static com.enn.iot.dtu.protocol.modbus.constant.NumberLimitEnum.*;

/**
 * 写指令请求对象
 *
 * @author Mr.Jia
 * @date 2022/7/21 12:47 PM
 */
@Data
@ToString
public class IotWriteCmdReq4Modbus extends AbstractIotCmdReq {

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
     * 下发指令使用：字节数
     */
    private Integer byteNumber;

    /**
     * 下发指令使用：下发的寄存器值
     */
    private Number addressWriteValue;

    /**
     * 测点配置列表
     */
    private List<IotCmdReqPoint4Modbus> pointList;

    /**
     * 写指令对应的读指令请求对象
     */
    private IotReadCmdReq4Modbus iotReadCmdReq4Modbus;

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

        if (!Arrays.asList(FUN_CODE_READ_05, FUN_CODE_READ_06, FUN_CODE_READ_10).contains(functionCode)) {
            appendErrorMessage(errorMap, "functionCode", "写指令只支持(05,06,10)");
        }

        if (registerStartAddress == null) {
            appendErrorMessage(errorMap, "registerStartAddress", "不能为null");
        }

        if (registerNumber == null) {
            appendErrorMessage(errorMap, "registerNumber", "不能为null");
        }

        if (functionCode == FUN_CODE_READ_10) {
            // 0x0001 至 0x0078
            if (registerNumber < 1 || registerNumber > 120) {
                appendErrorMessage(errorMap, "registerNumber", "超出有效范围[1,120]");
            }
        }

        if (byteNumber == null) {
            appendErrorMessage(errorMap, "byteNumber", "不能为null");
        }
        if (byteNumber != null) {
            if (registerNumber * 2 != byteNumber) {
                appendErrorMessage(errorMap, "byteNumber", "写多个寄存器指令的寄存器个数和字节数不对应");
            }
        }

        if (pointList == null) {
            appendErrorMessage(errorMap, "pointList", "不能为null");
        }

        if (registerStartAddress != null) {
            if ((registerStartAddress < 0) || (registerStartAddress > 65535)) {
                appendErrorMessage(errorMap, "registerStartAddress", "寄存器开始地址不合法，超出有效范围[0,65535]");
            }
            int endRegisterStartAddress = calculateRequestRegisterStartAddress() + calculateRequestRegisterNumber() - 1;
            if (endRegisterStartAddress > 65535) {
                appendErrorMessage(errorMap, "endRegisterAddress", "将要下发的寄存器截止地址超过最大数据地址，有效范围[0,65535]");
            }
        }

        if (addressWriteValue == null) {
            appendErrorMessage(errorMap, "addressWriteValue", "下发的值不能为空");
        }

        if (pointList != null && pointList.size() > 0) {
            for (int i = 0, len = pointList.size(); i < len; i++) {
                final int index = i;
                Map<String, String> pointErrorMap = pointList.get(index).validate();
                pointErrorMap.forEach((property, errorMsg) -> appendErrorMessage(errorMap,
                    String.format("pointList[%d].%s", index, property), errorMsg));
            }
        }

        /*
          针对控制下行的时候，下发的值做校验。
         */
        if (!this.getReadonly() && addressWriteValue != null && pointList != null && pointList.size() > 0) {
            String message = "addressWriteValue";
            String text = "下发的值不合法，超出有效范围[%d,%d]";
            DataTypeEnum dataType = getInstance(pointList.get(0).getDataType());
            switch (dataType) {
                case BIT0:
                case BIT1:
                case BIT2:
                case BIT3:
                case BIT4:
                case BIT5:
                case BIT6:
                case BIT7:
                case BIT8:
                case BIT9:
                case BIT10:
                case BIT11:
                case BIT12:
                case BIT13:
                case BIT14:
                case BIT15:
                    if ((addressWriteValue.intValue() < 0) || addressWriteValue.intValue() > 1) {
                        appendErrorMessage(errorMap, message, String.format(text, 0, 1));
                    }
                    break;
                case INT16:
                    if ((addressWriteValue.intValue() < INT16_LIMIT.getMinValue())
                        || addressWriteValue.intValue() > INT16_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, INT16_LIMIT.getMinValue(), INT16_LIMIT.getMaxValue()));
                    }
                    break;
                case UINT16:
                    if ((addressWriteValue.intValue() < UINT16_LIMIT.getMinValue())
                        || addressWriteValue.intValue() > UINT16_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, UINT16_LIMIT.getMinValue(), UINT16_LIMIT.getMaxValue()));
                    }
                    break;
                case INT32:
                    if ((addressWriteValue.longValue() < INT32_LIMIT.getMinValue())
                        || addressWriteValue.longValue() > INT32_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, INT32_LIMIT.getMinValue(), INT32_LIMIT.getMaxValue()));
                    }
                    break;
                case UINT32:
                    if ((addressWriteValue.longValue() < UINT32_LIMIT.getMinValue())
                        || addressWriteValue.longValue() > UINT32_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, UINT32_LIMIT.getMinValue(), UINT32_LIMIT.getMaxValue()));
                    }
                    break;
                case INT64:
                    long longValue = addressWriteValue.longValue();
                    if ((longValue < INT64_LIMIT.getMinValue()) || longValue > INT64_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, INT64_LIMIT.getMinValue(), INT64_LIMIT.getMaxValue()));
                    }
                    break;
                case UINT64:
                    if ((addressWriteValue.longValue() < UINT64_LIMIT.getMinValue())
                        || addressWriteValue.longValue() > UINT64_LIMIT.getMaxValue()) {
                        appendErrorMessage(errorMap, message,
                            String.format(text, UINT64_LIMIT.getMinValue(), UINT64_LIMIT.getMaxValue()));
                    }
                    break;
                default:
                    break;
            }
        }
        return errorMap;
    }

    /**
     * 最终的长度：额外向前读取寄存器个数 + 读取寄存器个数 + 额外向后读取寄存器个数
     */
    public int calculateRequestRegisterNumber() {
        if (registerNumber == null) {
            registerNumber = 0;
        }
        return this.registerNumber;
    }

    /**
     * modbus 通讯时，寄存器开始地址从0开始
     */
    public int calculateRequestRegisterStartAddress() {
        return registerStartAddress;
    }

    @Override
    public IotWriteCmdReq4Modbus clone() {
        IotWriteCmdReq4Modbus newInstance = (IotWriteCmdReq4Modbus)super.clone();
        List<IotCmdReqPoint4Modbus> newPointList = new LinkedList<>();
        for (IotCmdReqPoint4Modbus point : this.pointList) {
            newPointList.add(point.clone());
        }
        newInstance.setPointList(newPointList);
        return newInstance;
    }
}
