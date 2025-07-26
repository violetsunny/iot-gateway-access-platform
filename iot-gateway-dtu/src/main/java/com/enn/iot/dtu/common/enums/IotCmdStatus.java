package com.enn.iot.dtu.common.enums;

import lombok.Getter;

@Getter
public enum IotCmdStatus {
    /**
     * 空闲状态，没有正在执行和待执行指令（读指令和写指令队列都为空）。
     */
    IDLE_WITHOUT_COMMAND(100),
    /**
     * 就绪状态，等待指令执行
     */
    READY_FOR_SENDING(201),
    /**
     * 等待应答状态，已发送请求报文,等待应答报文
     */
    SENT_AND_WAITING_RESPONSE(202),
    /**
     * 应答报文帧检测通过
     */
    DETECT_SUCCESS(203),
    /**
     * 应答报文解析成功
     */
    DECODE_SUCCESS(204),
    /**
     * 执行结束，执行成功，且该指令不是重试指令
     */
    END_SUCCESS_IS_NOT_RETRY(299),

    /**
     * 执行结束，执行成功，该指令是重试指令
     */
    END_SUCCESS_IS_RETRY(300),
    /**
     * 帧检测失败,继续等待应答报文
     */
    DETECT_ERROR_AND_WAITING_RESPONSE(301),
    /**
     * 解析失败,继续等待应答报文
     */
    DECODE_ERROR_AND_WAITING_RESPONSE(302),
    /**
     * 执行结束，应答超时
     */
    END_RESPONSE_TIMEOUT(401),

    /**
     * 指令重试
     */
    CMD_RETRY(402),
    /**
     * 执行结束，服务器内部未知错误
     */
    END_INACTIVE(499),
    /**
     * 执行结束，服务器内部未知错误
     */
    END_UNKNOWN_ERROR(500);

    private final Integer value;

    IotCmdStatus(Integer value) {
        this.value = value;
    }
}
