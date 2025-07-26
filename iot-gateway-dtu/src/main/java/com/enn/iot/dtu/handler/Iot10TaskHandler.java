package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.devicecmdstatus.dto.DeviceCmdHealthStatus;
import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.common.outer.msg.util.IotOutMessageUtils;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.service.CmdExecuteService;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class Iot10TaskHandler extends SimpleChannelInboundHandler<IotCmdResp> {

    private final CmdExecuteService cmdExecService;

    public Iot10TaskHandler(CmdExecuteService cmdExecService) {
        this.cmdExecService = cmdExecService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IotCmdResp cmdResp) {
        if (log.isTraceEnabled()) {
            log.trace(Log.context(ctx) + "[10] 应答报文解析成功，执行下条指令，解析结果:\n{}", JsonUtils.writeValueAsString(cmdResp));
        }
        // 更新指令状态
        // 当前执行成功的指令的是否为重试指令
        IotCmdStatus cmdStatus = IotChannelContextUtil.Cmd.getPreviousCmdStatus(ctx);
        if (cmdStatus != IotCmdStatus.CMD_RETRY) {
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.END_SUCCESS_IS_NOT_RETRY);
        } else {
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.END_SUCCESS_IS_RETRY);
        }

        // 更新当前从站设备的健康状态
        AbstractIotCmdReq curCmd = IotChannelContextUtil.Cmd.getCurrentCmd(ctx);
        DeviceCmdHealthStatus deviceCmdStatus = IotChannelContextUtil.DeviceCmdStatus.getDeviceCmdHealthStatus(ctx,
                curCmd.getStationId(), curCmd.getTrdPtyCode());
        if (deviceCmdStatus == null) {
            deviceCmdStatus = new DeviceCmdHealthStatus();
            deviceCmdStatus.setStationId(curCmd.getStationId());
            deviceCmdStatus.setTrdPtyCode(curCmd.getTrdPtyCode());
            deviceCmdStatus.setHasCmdHealth(true);
            IotChannelContextUtil.DeviceCmdStatus.setDeviceCmdHealthStatus(ctx, deviceCmdStatus);
        } else {
            if (!deviceCmdStatus.isDeviceHealth()) {
                deviceCmdStatus.setDeviceHealth(true);
                deviceCmdStatus.setHasCmdHealth(true);
                log.info(IotChannelContextUtil.Log.context(ctx) + "[10]从站设备stationId:{}, trdPtyCode:{}通讯恢复",
                        curCmd.getStationId(), curCmd.getTrdPtyCode());
                String logInfo = String.format("%s从站设备stationId:%s, trdPtyCode:%s,通讯异常-->正常！",
                        IotChannelContextUtil.Log.context(ctx), curCmd.getStationId(), curCmd.getTrdPtyCode());
                IotChannelContextUtil.DeviceCmdStatus.setDeviceStatusLog(ctx, logInfo);
                IotChannelContextUtil.DeviceCmdStatus.setDeviceCmdHealthStatus(ctx, deviceCmdStatus);
            }
        }

        ctx.fireChannelRead(IotOutMessageUtils.getDataFromCmdResp(cmdResp));
        // 执行下一条指令
        cmdExecService.executeNextCmdIf(ctx);
    }
}

