package com.enn.iot.dtu.protocol.api.enums;

import lombok.Getter;

/**
 * 1.不合法帧报文; 2.半包报文; 3.粘包报文
 **/
@Getter
public enum IotDecodeCodeEnum {

    /**
     *
     */
    SUCCESS(200, "success"),
    /**
     *
     */
    OK_NEEDS_MORE_FRAME_LENGTH(201, "上行下行报文整体帧长度小于期望"),
    /**
     *
     */
    OK_NEEDS_MORE_FRAME_HEAD_LENGTH(202, "报文头的长度小于期望"),
    /**
     *
     */
    ERROR_CRC(401, "CRC校验错误"),
    /**
     *
     */
    ERROR_DEVICE_ADDRESS_NOT_EXPECTED(402, "上行下行从站地址校验不一致"),
    /**
     *
     */
    ERROR_FRAME_LENGTH_TOO_LONG(403, "报文超出合法长度"),
    /**
     *
     */
    ERROR_MODBUS_FUNCTION_CODE_PLUS_80(410, "应答错误码，加0x80错误码"),
    /**
     *
     */
    ERROR_MODBUS_FUNCTION_CODE_UNKNOWN(411, "不存在的功能码,目前仅支持0x01、0x02、0x03、0x04、0x05、0x06、0x10功能码"),
    /**
     *
     */
    ERROR_MODBUS_FUNCTION_CODE_NOT_EXPECTED(412, "上行下行功能码校验不符合期望的功能码"),

    ERROR_MODBUS_SLAVE_ADDRESS_RESOLUTION(413, "modbus从站地址解析错误"),

    ERROR_MODBUS_FUN_CODE_RESOLUTION(415, "modbus功能码解析错误"),

    ERROR_MODBUS_BYTE_NUM_RESOLUTION(416, "modbus字节数解析错误"),

    ERROR_MODBUS_ADDRESS_NUM_RESOLUTION(417, "modbus从站地址超出范围"),

    ERROR_MODBUS_DATA_VALUE_LENGTH_NOT_EXPECTED(418, "modbus数据域长度与期望不一致"),
    /**
     *
     */
    ERROR_DLT645_FRAME_FIRST_0X68_NOT_FOUND(420, "帧头错误，未找到第一个0x86"),

    ERROR_DLT645_FRAME_SECOND_0X68_NOT_FOUND(421, "帧头错误，第二个0x86位置数据错误"),
    /**
     *
     */
    ERROR_DLT645_FRAME_TAIL_NOT_0X16(422, "帧尾错误，帧尾不是0x16"),

    ERROR_DLT645_DEV_ADDR_NOT_EXPECTED(423, "应答报文读表地址与期望不符"),
    /**
     *
     */
    ERROR_DLT645_CONTROL_CODE_SLAVE_ERROR(424, "应答控制码中从站异常"),

    ERROR_DLT645_CONTROL_CODE_FUNCTION_CODE_NOT_EXPECTED(425, "应答报文功能码与期望不符"),

    ERROR_DLT645_CONTROL_DIRECT_NOT_EXPECTED(426, "应答报文控制码中的传送方向与期望不符"),

    ERROR_DLT645_CONTROL_SLAVE_ERROR(427, "应答报文中控制码的从站异常"),

    ERROR_DLT645_DATA_ID_UNKNOWN(428, "不支持的数据标识"),

    ERROR_DLT645_DATA_VALUE_LENGTH_NOT_EXPECTED(429, "应答报文数据标识对应数据长度与实际不符"),

    ERROR_DLT645V97_LEAD_BYTE_LENGTH_NOT_1_TO_4(430, "645-97协议前导字节长度不是1到4个字节"),

    ERROR_DLT645V07_LEAD_BYTE_LENGTH_NOT_4(431, "645-07协议前导字节长度不是4个字节"),

    ERROR_DLT645_DATA_LENGTH_NOT_EXPECTED(432, "数据域长度不合法，数据域长度为0~200"),

    ERROR_MODBUS_ADDRESS_LENGTH_NOT_EXPECTED(433, "写指令应答报文寄存器地址与期望不符"),

    ERROR_MODBUS_NUMBER_LENGTH_NOT_EXPECTED(434, "写指令应答报文寄存器数量与期望不符"),

    ERROR_CONFIG_INVALIDATE(500, "采集配置不合法"),

    ERROR_CONFIG_POINT_LIST_EMPTY(501, "测点配置集合不能为空"),

    ERROR_DETECTION_FRAME_LENGTH_ERROR(900, "帧检测结果长度越界"),

    ERROR_UNKNOWN(999, "未知错误");

    private final Integer value;
    private final String msg;

    IotDecodeCodeEnum(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }
}
