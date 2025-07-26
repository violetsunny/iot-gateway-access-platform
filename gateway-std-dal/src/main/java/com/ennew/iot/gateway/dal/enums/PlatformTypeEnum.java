/**
 * llkang.com Inc.
 * Copyright (c) 2010-2024 All Rights Reserved.
 */
package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 按照gateway的connectors
 * @author qk
 * @version $Id: ApiTypeEnum, v 0.1 2024/3/15 11:13 qk Exp $
 */
@Getter
@AllArgsConstructor
public enum PlatformTypeEnum {

    CTWING("电信AEP",   1, "三方平台"),

    REQUEST("三方接口", 2, "三方平台"),

    MODBUS_TCP("ModBusTCP", 3, "ModBus");

    final String name;
    final int code;
    final String tag;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Integer getCode(String name) {
        for (PlatformTypeEnum e : PlatformTypeEnum.values()) {
            if (e.getName().equals(name)) {
                return e.getCode();
            }
        }
        return null;
    }

    public static String getName(Integer code) {
        for (PlatformTypeEnum e : PlatformTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }

    public static List<Integer> getCodeList() {
        List<Integer> list = new ArrayList<>();
        for (PlatformTypeEnum e : PlatformTypeEnum.values()) {
            list.add(e.getCode());
        }
        return list;
    }


    public static PlatformTypeEnum parse(Integer code){
        return Arrays.stream(PlatformTypeEnum.values()).filter(e -> Objects.equals(e.getCode(), code))
                .findAny()
                .orElse(null);
    }

}
