package com.ennew.iot.gateway.dal.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;


@Getter
public enum ModbusDataTypeEnum {
    /**
     *
     */
    BIT0("bit0", 1),
    /**
     *
     */
    BIT1("bit1", 1),
    /**
     *
     */
    BIT2("bit2", 1),
    /**
     *
     */
    BIT3("bit3", 1),
    /**
     *
     */
    BIT4("bit4", 1),
    /**
     *
     */
    BIT5("bit5", 1),
    /**
     *
     */
    BIT6("bit6", 1),
    /**
     *
     */
    BIT7("bit7", 1),
    /**
     *
     */
    BIT8("bit8", 1),
    /**
     *
     */
    BIT9("bit9", 1),
    /**
     *
     */
    BIT10("bit10", 1),
    /**
     *
     */
    BIT11("bit11", 1),
    /**
     *
     */
    BIT12("bit12", 1),
    /**
     *
     */
    BIT13("bit13", 1),
    /**
     *
     */
    BIT14("bit14", 1),
    /**
     *
     */
    BIT15("bit15", 1),
    /**
     *
     */
    INT8("int8", 1),
    /**
     *
     */
    UINT8("uint8", 1),
    /**
     *
     */
    INT16("int16", 1),
    /**
     *
     */
    UINT16("uint16", 1),
    /**
     *
     */
    INT24("int24", 2),
    /**
     *
     */
    UINT24("uint24", 2),
    /**
     *
     */
    INT32("int32", 2),
    /**
     *
     */
    UINT32("uint32", 2),
    /**
     *
     */
    INT48("int48", 3),
    /**
     *
     */
    UINT48("uint48", 3),
    /**
     *
     */
    INT64("int64", 4),
    /**
     *
     */
    UINT64("uint64", 4),
    /**
     *
     */
    FLOAT32("float32", 2),
    /**
     *
     */
    FLOAT64("float64", 4),
    /**
     *
     */
    TIME48("time48", 3),
    /**
     *
     */
    TIME64("time64", 4);


    private final String value;
    private final Integer byteNum;

    ModbusDataTypeEnum(String value, Integer byteNum) {
        this.value = value;
        this.byteNum = byteNum;
    }
}
