package com.enn.iot.dtu.protocol.api.enums;

import lombok.Getter;

@Getter
public enum ProtocolTypeEnum {
    /**
     *
     */
    MODBUS_RTU("MODBUS-RTU", "1"),
    /**
     *
     */
    DLT_645_97("DL/T 645(97)", "1"),
    /**
     *
     */
    DLT_645_07("DL/T 645(07)", "1"),
    /**
     *
     */
    LNRTTYN("LNRTTYN", "1"),
    /**
     *
     */
    PPI("PPI", "2");

    private final String value;
    private final String defaultAddress;

    ProtocolTypeEnum(String value, String defaultAddress) {
        this.value = value;
        this.defaultAddress = defaultAddress;
    }

    public static ProtocolTypeEnum getProtocolType(String protocolName) {
        if (protocolName == null || protocolName.length() == 0) {
            return ProtocolTypeEnum.MODBUS_RTU;
        }
        if (protocolName.length() >= 6 && "MODBUS".equals(protocolName.substring(0, 6))) {
            return ProtocolTypeEnum.MODBUS_RTU;
        }

        if ("DL/T 645(97)".equals(protocolName)) {
            return ProtocolTypeEnum.DLT_645_97;
        }

        if ("DL/T 645(07)".equals(protocolName)
                || (protocolName.length() >= 4 && "DL/T".equals(protocolName.substring(0, 4)))) {
            return ProtocolTypeEnum.DLT_645_07;
        }

        if ("LNRTTYN".equals(protocolName)) {
            return ProtocolTypeEnum.LNRTTYN;
        }

        if (protocolName.length() >= 3 && "PPI".equals(protocolName.substring(0, 3))) {
            return ProtocolTypeEnum.PPI;
        }
        return ProtocolTypeEnum.MODBUS_RTU;
    }
}
