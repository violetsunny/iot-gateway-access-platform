package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.factory.IotProtocolCodecFactory;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Cmd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@ChannelHandler.Sharable
public class Iot08ProtocolEncoder extends MessageToByteEncoder<AbstractIotCmdReq> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq, ByteBuf out) {
        String communicationProtocol = cmdReq.getCommcPrcl();
        ProtocolTypeEnum protocolType = ProtocolTypeEnum.getProtocolType(communicationProtocol);
        IotProtocolCodec codec = IotProtocolCodecFactory.getInstance(protocolType);
        AbstractIotCmdReq previousErrorCmdReq = null;
        if (Cmd.getPreviousCmdStatus(ctx) == IotCmdStatus.END_RESPONSE_TIMEOUT
                || Cmd.getPreviousCmdStatus(ctx) == IotCmdStatus.END_SUCCESS_IS_RETRY) {
            previousErrorCmdReq = Cmd.getPreviousCmd(ctx);
            if (previousErrorCmdReq != null
                    && !StringUtils.equals(cmdReq.getCommcPrcl(), previousErrorCmdReq.getCommcPrcl())) {
                previousErrorCmdReq = null;
            }
            // 如果延迟防御为 0 关: 那就设置previousErrorCmdReq为 null,不改变寄存器的数量
            if (cmdReq.getDelayDefensive() != null && cmdReq.getDelayDefensive() == 0) {
                previousErrorCmdReq = null;
            }
        }
        ByteBuf frameBuf = null;
        try {
            // 识别上一条指令是否执行失败，如果失败需要传第二个参数。
            frameBuf = codec.encode(cmdReq, previousErrorCmdReq, ctx.alloc());
            if (frameBuf == null || !frameBuf.isReadable()) {
                Cmd.setCurrentCmdReqFrameHex(ctx, StringUtils.EMPTY);
                log.error(IotChannelContextUtil.Log.context(ctx) + "[8]编码失败，编码结果为空！");
            } else {
                Cmd.setCurrentCmdReqFrameHex(ctx, ByteBufUtil.hexDump(frameBuf));
                // [全链路]日志埋点 此条日志不能轻易删除!
                log.info("[全链路] 会话标识：{}，下行报文：{}，系统编码：{}，网关标识：{}，设备标识：{}", ctx.channel().id().asShortText(),
                        ByteBufUtil.hexDump(frameBuf), cmdReq.getStationId(), cmdReq.getGatewaySn(),
                        cmdReq.getTrdPtyCode());
                out.writeBytes(frameBuf);
            }
        } catch (Exception e) {
            Cmd.setCurrentCmdReqFrameHex(ctx, StringUtils.EMPTY);
            log.error(IotChannelContextUtil.Log.context(ctx) + "[8]编码失败，未识别异常！", e);
        } finally {
            if (frameBuf != null) {
                ReferenceCountUtil.release(frameBuf);
            }
        }
    }
}
