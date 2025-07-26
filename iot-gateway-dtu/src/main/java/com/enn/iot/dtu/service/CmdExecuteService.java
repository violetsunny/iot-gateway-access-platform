package com.enn.iot.dtu.service;

import io.netty.channel.ChannelHandlerContext;

public interface CmdExecuteService {

    /**
     * 执行下一条指令
     *
     * @param ctx
     */
    void executeNextCmdIf(ChannelHandlerContext ctx);

    /**
     * 重试命令
     *
     * @param ctx
     */
    void retryCmd(ChannelHandlerContext ctx);

    /**
     * 如果没有执行中的指令，执行下一条指令，
     *
     * @param gatewaySn
     */
    void executeNextCmdIf(String gatewaySn);
}
