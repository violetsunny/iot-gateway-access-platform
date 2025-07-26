package com.ennew.iot.gateway.core.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CloudGatewayModbusPointBO extends CloudGatewayPointBO {

    /**
     * 从站地址
     */
    private Integer slaveAddress;


    /**
     * 功能码
     */
    private Integer functionCode;


    /**
     * 寄存器地址
     */
    private Integer registerAddress;


    /**
     * 数据类型
     */
    private String dataType;


    /**
     * 字节顺序
     */
    private String byteOrder;


    /**
     * 读写类型
     */
    private String rw;


}
