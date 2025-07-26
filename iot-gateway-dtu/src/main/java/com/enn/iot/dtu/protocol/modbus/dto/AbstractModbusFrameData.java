package com.enn.iot.dtu.protocol.modbus.dto;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import com.enn.iot.dtu.protocol.modbus.utils.ModbusRtuUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.*;
import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;
import static com.enn.iot.dtu.protocol.modbus.utils.ModbusGenerateCmdUtil.logContext;

/**
 * 响应指令报文结构体
 *
 * @author Mr.Jia
 * @date 2022/2/23 10:57
 */
@Slf4j
@Data
@ToString
@EqualsAndHashCode()
public abstract class AbstractModbusFrameData {

    /**
     * 从站地址
     */
    public int deviceAddress;

    /**
     * 功能码, 0x01,0x02,0x03,0x04
     */
    public int functionCode;

    /**
     * 异常码
     */
    public int errorCode;

    /**
     * 不包括crc的报文
     */
    public byte[] frameDataWithoutCrc;

    /**
     * crc的报文
     */
    public byte[] crc;

    /**
     * 帧读取结果
     */
    public IotDecodeCodeEnum resultCode;

    /**
     * 公共的解析方法
     *
     * @param respByteBuf 缓冲区
     * @author Mr.Jia
     * @date 2022/2/23 15:36
     */
    public void analysis(ByteBuf respByteBuf) {
        int startIndex = respByteBuf.readerIndex();
        // 从站地址, 1 字节
        if (respByteBuf.isReadable()) {
            this.deviceAddress = respByteBuf.readUnsignedByte();
        } else {
            this.resultCode = OK_NEEDS_MORE_FRAME_LENGTH;
            return;
        }
        // Modbus地址范围在0~247之间
        if (this.deviceAddress < 1 || 247 < this.deviceAddress) {
            this.resultCode = ERROR_MODBUS_ADDRESS_NUM_RESOLUTION;
            return;
        }

        // 功能码, 1 字节
        if (respByteBuf.isReadable()) {
            this.functionCode = respByteBuf.readUnsignedByte();
            if (this.validateForDetectFunctionCode()) {
                this.resultCode = ERROR_MODBUS_FUNCTION_CODE_UNKNOWN;
                return;
            }
        } else {
            this.resultCode = OK_NEEDS_MORE_FRAME_LENGTH;
            return;
        }

        // 字节数,1 字节
        int frameDataLengthWithoutCrc;
        // crc校验
        if (this.isErrorFunctionCode()) {
            frameDataLengthWithoutCrc = RTU_HEAD_LEN;
            if (respByteBuf.readableBytes() > 1) {
                this.errorCode = respByteBuf.readUnsignedByte();
            } else {
                this.resultCode = OK_NEEDS_MORE_FRAME_LENGTH;
                return;
            }
        } else {
            int dataLength = this.analysisExtraFrameData(respByteBuf);
            if (dataLength <= 0) {
                this.resultCode = OK_NEEDS_MORE_FRAME_LENGTH;
                return;
            }
            frameDataLengthWithoutCrc = dataLength;
        }

        // 获取包括crc的报文
        this.frameDataWithoutCrc = new byte[frameDataLengthWithoutCrc];
        respByteBuf.getBytes(startIndex, this.frameDataWithoutCrc);

        if (respByteBuf.readableBytes() >= 2) {
            this.crc = new byte[RTU_CRC_LEN];
            respByteBuf.readBytes(this.crc);
            if (this.validateForDetectCrc()) {
                this.resultCode = ERROR_CRC;
            } else {
                this.resultCode = SUCCESS;
            }
        } else {
            this.resultCode = OK_NEEDS_MORE_FRAME_LENGTH;
        }
    }

    public int calculateFrameDataLength() {
        return this.frameDataWithoutCrc.length + 2;
    }

    public boolean isResultSuccess() {
        return resultCode == SUCCESS;
    }

    public boolean isResultOk() {
        return resultCode == OK_NEEDS_MORE_FRAME_LENGTH;
    }

    public boolean validateForDetectCrc() {
        byte[] expectedCrc = ModbusRtuUtil.getCrc16ToByte(this.frameDataWithoutCrc);
        return expectedCrc[0] != crc[0] || expectedCrc[1] != crc[1];
    }

    public boolean isErrorFunctionCode() {
        return this.functionCode >= FUN_CODE_READ01_ERROR;
    }

    /**
     * 解析需要的校验
     *
     * @param abstractIotCmdReq 指令对象
     * @return com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum
     * @author Mr.Jia
     * @date 2022/2/23 15:37
     */
    public IotDecodeCodeEnum validateForDecode(AbstractIotCmdReq abstractIotCmdReq) {
        // 上行下行从站地址校验
        if (!Integer.valueOf(deviceAddress).equals(Integer.parseInt(abstractIotCmdReq.getCommcAddr()))) {
            return ERROR_DEVICE_ADDRESS_NOT_EXPECTED;
        }
        if (functionCode >= FUN_CODE_READ01_ERROR) {
            if (log.isWarnEnabled()) {
                log.warn(
                    logContext(abstractIotCmdReq.getGatewaySn()) + "[Modbus] 设备异常应答，功能码: 0x{}, 异常码: 0x{}, 指令请求: {}",
                    Integer.toHexString(functionCode), Integer.toHexString(errorCode), abstractIotCmdReq);
            }
            // 如果是异常码81就直接返回错误
            return ERROR_MODBUS_FUNCTION_CODE_PLUS_80;
        }
        return SUCCESS;
    }

    /**
     * 检查支持的功能码
     *
     * @return boolean
     * @author Mr.Jia
     * @date 2022/2/23 15:30
     */
    public abstract boolean validateForDetectFunctionCode();

    /**
     * 返回除了crc报文的长度
     *
     * @param respByteBuf 缓冲区
     * @return int 返回除了crc报文的长度
     * @author Mr.Jia
     * @date 2022/2/23 15:41
     */
    public abstract int analysisExtraFrameData(ByteBuf respByteBuf);
}