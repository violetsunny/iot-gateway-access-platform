package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.*;
import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;

/**
 * 写指令响应报文结构体
 * <p>
 * 05、06： 从站地址、功能码、寄存器地址、寄存器值、crc值
 * <p>
 * 10： 从站地址、功能码、寄存器地址、寄存器数量、crc值
 *
 * @author Mr.Jia
 * @date 2022/2/23 10:57
 */
@Data
@ToString
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ModbusWriteFrameData extends AbstractModbusFrameData {

    /**
     * （05、06、10）寄存器地址
     */
    private int registerStartAddress;

    /**
     * （10）寄存器数量
     */
    private int registerNumber;

    /**
     * （05、06）寄存器值
     */
    private Number addressWriteValue;

    public ModbusWriteFrameData readInstance(ByteBuf respByteBuf) {
        super.analysis(respByteBuf);
        return this;
    }

    /**
     * 支持06\05\10功能码以及对应的异常码
     *
     * @return boolean
     * @author Mr.Jia
     * @date 2022/1/6 18:58
     */
    @Override
    public boolean validateForDetectFunctionCode() {
        return !Arrays.asList(FUN_CODE_READ_05, FUN_CODE_READ_06, FUN_CODE_READ_10, FUN_CODE_WRITE85_ERROR,
            FUN_CODE_WRITE86_ERROR, FUN_CODE_WRITE90_ERROR).contains(this.functionCode);
    }

    /**
     * 解析额外的数据
     *
     * @param respByteBuf 缓冲区
     * @return int 返回除了crc报文的长度
     * @author Mr.Jia
     * @date 2022/2/23 15:41
     */
    @Override
    public int analysisExtraFrameData(ByteBuf respByteBuf) {
        if (respByteBuf.isReadable()) {
            if (FUN_CODE_READ_05 == this.functionCode || FUN_CODE_READ_06 == this.functionCode) {
                this.registerStartAddress = respByteBuf.readUnsignedShort();
                this.addressWriteValue = respByteBuf.readUnsignedShort();
                this.registerNumber = FUN_CODE_01_02_DEFAULT_VALUE;
                return RTU_HEAD_WRITE_ONE_CMD_LEN;
            } else if (FUN_CODE_READ_10 == this.functionCode) {
                this.registerStartAddress = respByteBuf.readUnsignedShort();
                this.registerNumber = respByteBuf.readUnsignedShort();
                return RTU_HEAD_WRITE_ONE_CMD_LEN;
            }
        }
        return 0;
    }

    /**
     * 解析需要的校验
     *
     * @param abstractIotCmdReq 指令对象
     * @return com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum
     * @author Mr.Jia
     * @date 2022/2/23 15:37
     */
    @Override
    public IotDecodeCodeEnum validateForDecode(AbstractIotCmdReq abstractIotCmdReq) {
        IotDecodeCodeEnum codeEnum = super.validateForDecode(abstractIotCmdReq);
        if (codeEnum != SUCCESS) {
            return codeEnum;
        }
        if (abstractIotCmdReq instanceof IotWriteCmdReq4Modbus) {
            IotWriteCmdReq4Modbus writeCmdReq = (IotWriteCmdReq4Modbus)abstractIotCmdReq;
            Integer functionCode = writeCmdReq.getFunctionCode();
            // 上行下行功能码校验
            if (this.functionCode != functionCode) {
                return ERROR_MODBUS_FUNCTION_CODE_NOT_EXPECTED;
            }
            // 检查寄存器地址是否匹配对应
            Integer registerStartAddress = writeCmdReq.getRegisterStartAddress();
            if (registerStartAddress != this.registerStartAddress) {
                return ERROR_MODBUS_ADDRESS_LENGTH_NOT_EXPECTED;
            }

            // 当功能码为 0x10 的时候，检查寄存器数量规则；
            if (this.functionCode == FUN_CODE_READ_10) {
                // 检查寄存器数量是否匹配对应
                Integer registerNumber = writeCmdReq.getRegisterNumber();
                if (registerNumber != this.registerNumber) {
                    return ERROR_MODBUS_NUMBER_LENGTH_NOT_EXPECTED;
                }
            }
        }
        return SUCCESS;
    }
}