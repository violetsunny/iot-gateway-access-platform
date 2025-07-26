/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.client.enums;

import lombok.Getter;

/**
 * @author kanglele
 * @version $Id: MessageType, v 0.1 2023/2/3 12:07 kanglele Exp $
 */
@Getter
public enum MessageType {
    /**
     * <pre>
     * 登录请求
     * </pre>
     *
     * <code>LOGIN_REQ = 1;</code>
     */
    LOGIN_REQ,
    /**
     * <pre>
     * 登录响应
     * </pre>
     *
     * <code>LOGIN_RSP = 2;</code>
     */
    LOGIN_RSP,
    /**
     * <pre>
     * 数据上报请求
     * </pre>
     *
     * <code>REPORT_REQ = 3;</code>
     */
    REPORT_REQ,
    /**
     * <pre>
     * 数据上报响应
     * </pre>
     *
     * <code>REPORT_RSP = 4;</code>
     */
    REPORT_RSP,
    /**
     * <pre>
     * 下发指令
     * </pre>
     *
     * <code>OPERATION_REQ = 5;</code>
     */
    OPERATION_REQ,
    /**
     * <pre>
     * 指令相应
     * </pre>
     *
     * <code>OPERATION_RSP = 6;</code>
     */
    OPERATION_RSP,

    /**
     * <pre>
     * 通知消息
     * </pre>
     *
     * <code>NOTIFICATION = 5;</code>
     */
    NOTIFICATION,
    /**
     * 历史数据上报
     */
    HISTORY_REQ,
    /**
     * 工况上报
     */
    STATUS_REQ,
    /**
     * 信息上报
     */
    INFO_REQ,
    /**
     * 事件上报
     */
    EVENT_REQ
}
