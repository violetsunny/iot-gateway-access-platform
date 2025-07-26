package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.*;
import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;

/**
 * 读指令响应报文结构体
 *
 * @author Mr.Jia
 * @date 2022/2/23 10:57
 */
@Data
@ToString
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ModbusReadFrameData extends AbstractModbusFrameData {

    /**
     * 数据域长度
     */
    public int dataLength;

    /**
     * 数据域：数据值<br/>
     */
    public byte[] dataValue;

    public ModbusReadFrameData readInstance(ByteBuf respByteBuf) {
        super.analysis(respByteBuf);
        return this;
    }

    /**
     * 检查支持的功能码，支持01、02、03、04 功能码以及对应的异常码
     *
     * @return boolean
     * @author Mr.Jia
     * @date 2022/2/23 15:30
     */
    @Override
    public boolean validateForDetectFunctionCode() {
        // 支持01、02、03、04 功能码
        if (FUN_CODE_READ_01 <= this.functionCode && this.functionCode <= FUN_CODE_READ_04) {
            return false;
        }
        // 支持81、82、83、84 功能码
        return FUN_CODE_READ01_ERROR > this.functionCode || this.functionCode > FUN_CODE_READ04_ERROR;
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
            this.dataLength = respByteBuf.readUnsignedByte();
            if (respByteBuf.isReadable(this.dataLength)) {
                this.dataValue = new byte[this.dataLength];
                respByteBuf.readBytes(this.dataValue);
                return RTU_HEAD_LEN + this.dataLength;
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
        if (abstractIotCmdReq instanceof IotReadCmdReq4Modbus) {
            IotReadCmdReq4Modbus reqCmd = (IotReadCmdReq4Modbus)abstractIotCmdReq;
            Integer functionCode = reqCmd.getFunctionCode();
            // 上行下行功能码校验
            if (this.functionCode != functionCode) {
                return ERROR_MODBUS_FUNCTION_CODE_NOT_EXPECTED;
            }
            // 上行下行期望帧长度校验
            if (this.dataLength != reqCmd.calculateResponseBytesNumber()) {
                return ERROR_MODBUS_DATA_VALUE_LENGTH_NOT_EXPECTED;
            }
        }
        return SUCCESS;
    }
}