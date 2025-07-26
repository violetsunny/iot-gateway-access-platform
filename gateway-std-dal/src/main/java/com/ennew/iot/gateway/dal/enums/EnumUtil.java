package com.ennew.iot.gateway.dal.enums;

public class EnumUtil {
    public static <E extends Enum<E> & EnumType> E getValue(Class<E> enumClass, String value) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getName().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
