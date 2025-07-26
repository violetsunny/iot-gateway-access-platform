package com.enn.iot.dtu.common.msg.enums;

import lombok.Getter;

/**
 * 指令回执结果枚举
 * 
 * @author Mr.Jia
 * @date 2022/8/11 3:41 PM
 */
@Getter
public enum IotRespondCmdStatus {
    /**
     * 成功：succ
     */
    SUCCESS("succ", "执行成功"),
    /**
     * 失败：fail
     */
    FAIL("fail", "执行失败"),
    /**
     * 超时：timeout
     */
    TIMEOUT("timeout", "执行超时"), DETECTION_ERROR("detectionError", "帧检测失败"), DECODE_ERROR("decodeError", "解析失败");

    private final String value;
    private final String message;

    IotRespondCmdStatus(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
