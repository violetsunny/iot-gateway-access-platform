package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.common.event.IotDecodeErrorEvent;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.factory.IotProtocolCodecFactory;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Cmd;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Auth;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class Iot07ProtocolDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frameBuf) {
        IotCmdStatus cmdStatus = Cmd.getCmdStatus(ctx);
        boolean needDecode =
                // 前一帧报文帧检测成功，进入了拆包逻辑
                cmdStatus == IotCmdStatus.DETECT_SUCCESS
                        // 前一帧报文帧检测失败，继续等待应答报文
                        || cmdStatus == IotCmdStatus.DETECT_ERROR_AND_WAITING_RESPONSE
                        // 前一帧报文帧解析失败，继续等待应答报文
                        || cmdStatus == IotCmdStatus.DECODE_ERROR_AND_WAITING_RESPONSE;
        if (!needDecode) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[07]帧解析：帧解析失败，指令状态不是处于等待应答的状态！当前指令状态: {}", cmdStatus.toString());
            }
            return;
        }
        AbstractIotCmdReq cmdReq = Cmd.getCurrentCmd(ctx);
        if (cmdReq == null) {
            // 该应答报文没有与之相关联的指令请求对象
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[07]帧解析：帧解析失败，没有关联的指令请求对象！");
            }
            return;
        }
        String communicationProtocol = cmdReq.getCommcPrcl();
        ProtocolTypeEnum protocolType = ProtocolTypeEnum.getProtocolType(communicationProtocol);
        IotProtocolCodec codec = IotProtocolCodecFactory.getInstance(protocolType);
        try {
            IotCmdResp cmdResp = codec.decode(frameBuf.duplicate(), cmdReq);
            if (cmdResp.isSuccess()) {
                Cmd.setCmdStatus(ctx, IotCmdStatus.DECODE_SUCCESS);
                ctx.fireChannelRead(cmdResp);
            } else {
                String cmdRespFrameHex = ByteBufUtil.hexDump(frameBuf);
                if (log.isWarnEnabled()) {
                    log.warn(Log.context(ctx) + "[07]帧解析：解析失败！请求报文: 0x{}, 应答报文: 0x{}, 解析结果:\n{}",
                            Cmd.getCurrentCmdReqFrameHex(ctx), cmdRespFrameHex, JsonUtils.writeValueAsString(cmdResp));
                }
                // 解析失败事件
                ctx.fireUserEventTriggered(IotDecodeErrorEvent.error(Auth.getGatewaySn(ctx), cmdResp.getCode(),
                        "解析失败，原始报文: 0x" + cmdRespFrameHex));
            }
            // [全链路]日志埋点 此条日志不能轻易删除!
            log.info("[全链路] 会话标识：{}，上行报文：{}，系统编码：{}，网关标识：{}，设备标识：{}", ctx.channel().id().asShortText(),
                    ByteBufUtil.hexDump(frameBuf), cmdReq.getStationId(), cmdReq.getGatewaySn(), cmdReq.getTrdPtyCode());
        } catch (Exception e) {
            log.error(Log.context(ctx) + "[07]帧解析：解析异常！报文: 0x" + ByteBufUtil.hexDump(frameBuf) + ", 指令请求:"
                    + JsonUtils.writeValueAsString(cmdReq), e);
            // 解析异常事件
            ctx.fireUserEventTriggered(IotDecodeErrorEvent.error(Auth.getGatewaySn(ctx), null, "解析异常"));
        }
    }
}
