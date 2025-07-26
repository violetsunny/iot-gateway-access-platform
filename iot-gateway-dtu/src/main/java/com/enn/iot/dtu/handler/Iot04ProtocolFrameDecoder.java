package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.common.event.IotClearDecodeCumulatorEvent;
import com.enn.iot.dtu.common.event.IotDetectionErrorEvent;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.protocol.api.codec.IotDetectionResult;
import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.factory.IotProtocolCodecFactory;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Auth;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Cmd;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 可能面对的场景：半包报文，粘包报文<br/>
 * 针对半包报文，需要合包；针对粘包报文，需要拆包。<br/>
 * 经过此handler，输出的是完整的报文帧，包含：认证报文帧，协议报应答文帧。
 **/
@Slf4j
public class Iot04ProtocolFrameDecoder extends ByteToMessageDecoder {

    static final String BLACKLIST_TAIL_NEWLINE = ByteBufUtil.hexDump("\r\n".getBytes(StandardCharsets.UTF_8));

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 1、检测是否为黑名单中的可丢弃字节
        FrameDetectResult result = doBlacklistAndCheckEnd(ctx, in);
        if (result.detectFinished) {
            return;
        }
        // 2、检测是否为认证报文
        result = doAuthDetectAndCheckEnd(ctx, in, out);
        if (result.detectFinished) {
            return;
        }
        // 3、检测是否为心跳报文
        result = doHeartDetectAndCheckEnd(ctx, in, out);
        if (result.detectFinished) {
            return;
        }
        // 4、协议编解码器进行帧检测
        doProtocolDetect(ctx, in, out);
    }

    /**
     * 检测是否存在黑名单报文。<br/>
     * 如果命中黑名单字节直接抛弃；否则不做任何处理。<br/>
     * 如果处理后的字节长度为0，那么无需执行下一项帧检测。
     *
     * @param ctx
     *            ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     * @return 帧检测结果
     */
    private FrameDetectResult doBlacklistAndCheckEnd(ChannelHandlerContext ctx, ByteBuf in) {
        int readableBytes = in.readableBytes();
        // 去除换行符号，支持telnet测试。 "\r\n", "0d0a"
        int blackBytesLength = BLACKLIST_TAIL_NEWLINE.length() / 2;
        if (readableBytes >= blackBytesLength) {
            String tailHex = ByteBufUtil.hexDump(in, readableBytes - blackBytesLength, blackBytesLength);
            if (BLACKLIST_TAIL_NEWLINE.equals(tailHex)) {
                in.writerIndex(in.writerIndex() - blackBytesLength);
                if (log.isInfoEnabled()) {
                    log.info(IotChannelContextUtil.Log.context(ctx) + "[04]帧检测：识别出黑名单字节，抛弃尾部换行报文! 抛弃的报文: 0x{}", BLACKLIST_TAIL_NEWLINE);
                }
            }
        }
        // 如果没有可读字节数，则无需执行后面的检测项
        return FrameDetectResult.getInstance(!in.isReadable());
    }

    /**
     * 检测是否为认证报文。<br/>
     * 如果是则向后传播，无需执行下一项帧检测；否则执行下一项帧检测。
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     * @param out
     *            the List to which decoded messages should be added
     * @return 帧检测结果
     */
    private FrameDetectResult doAuthDetectAndCheckEnd(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (Auth.hasAuthed(ctx)) {
            return FrameDetectResult.getInstance(false);
        }
        // 还未认证，向后传播
        ByteBuf frameBuf = in.retainedDuplicate();
        out.add(frameBuf);
        in.skipBytes(in.readableBytes());
        return FrameDetectResult.getInstance(true);
    }

    /**
     * 检测是否为 DTU 心跳报文。<br/>
     * 如果是则向后传播，无需执行下一项帧检测；否则执行下一项帧检测。
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     * @param out
     *            the List to which decoded messages should be added
     * @return 帧检测结果
     */
    private FrameDetectResult doHeartDetectAndCheckEnd(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // TODO @lixiang 待实现
        return FrameDetectResult.getInstance(false);
    }

    /**
     * 检测是否为合法的通讯协议帧。<br/>
     * 如果是则向后传播；否则，产生检测失败事件。
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     * @param out
     *            the List to which decoded messages should be added
     */
    private void doProtocolDetect(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        IotCmdStatus cmdStatus = Cmd.getCmdStatus(ctx);
        boolean needDetect =
                // 已发送指令请求，等待应答报文
                cmdStatus == IotCmdStatus.SENT_AND_WAITING_RESPONSE
                        // 前一帧报文帧检测成功，进入了拆包逻辑
                        || cmdStatus == IotCmdStatus.DETECT_SUCCESS
                        // 前一帧报文帧检测失败，继续等待应答报文
                        || cmdStatus == IotCmdStatus.DETECT_ERROR_AND_WAITING_RESPONSE
                        // 前一帧报文帧解析失败，继续等待应答报文
                        || cmdStatus == IotCmdStatus.DECODE_ERROR_AND_WAITING_RESPONSE;
        if (!needDetect) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[04]帧检测：帧检测失败，指令状态不是处于等待应答的状态，抛弃报文！当前指令状态: {}, 报文: 0x{}",
                        cmdStatus.toString(), ByteBufUtil.hexDump(in));
            }
            // 消费掉全部字节，等效于抛弃所有字节
            in.skipBytes(in.readableBytes());
            return;
        }
        AbstractIotCmdReq cmdReq = Cmd.getCurrentCmd(ctx);
        if (cmdReq == null) {
            // 该应答报文没有与之相关联的指令请求对象
            log.error(Log.context(ctx) + "[04]帧检测：帧检测失败，没有关联的指令请求对象，抛弃报文！报文: 0x{}", ByteBufUtil.hexDump(in));
            // 消费掉全部字节，等效于抛弃所有字节
            in.skipBytes(in.readableBytes());
            return;
        }
        String communicationProtocol = cmdReq.getCommcPrcl();
        ProtocolTypeEnum protocolType = ProtocolTypeEnum.getProtocolType(communicationProtocol);
        IotProtocolCodec codec = IotProtocolCodecFactory.getInstance(protocolType);
        IotDetectionResult result;
        try {
            result = codec.detectResponseFrame(in.duplicate(), cmdReq);
        } catch (Exception e) {
            result = IotDetectionResult.error(IotDecodeCodeEnum.ERROR_UNKNOWN);
            log.error(Log.context(ctx) + "[04]帧检测：帧检测异常！", e);
        }
        boolean isValid = validateResultIndex(in, result);
        if (!isValid) {
            log.error(Log.context(ctx) + "[04]帧检测：帧检测错误，index不合法！检测结果: {}", JsonUtils.writeValueAsString(result));
            result.setDetectionCode(IotDecodeCodeEnum.ERROR_DETECTION_FRAME_LENGTH_ERROR);
            result.setMessage(IotDecodeCodeEnum.ERROR_DETECTION_FRAME_LENGTH_ERROR.getMsg());
        }
        IotDecodeCodeEnum code = result.getDetectionCode();
        switch (code) {
            case SUCCESS:
                // 包含了完整的帧报文（可以包含一帧报文或多帧报文）
                doDetectSuccess(ctx, in, out, result);
                return;
            case OK_NEEDS_MORE_FRAME_LENGTH:
            case OK_NEEDS_MORE_FRAME_HEAD_LENGTH:
                // 半包，等待剩余报文
                if (log.isWarnEnabled()) {
                    log.warn(Log.context(ctx) + "[04]帧检测：识别出半包报文！原始报文: 0x{}, 检测结果: {}", ByteBufUtil.hexDump(in),
                            JsonUtils.writeValueAsString(result));
                }
                return;
            default:
                // 如果报文帧校验未通过,抛弃全部报文
                doDetectError(ctx, in, result);
        }
    }

    private boolean validateResultIndex(ByteBuf in, IotDetectionResult result) {
        int startIndex = result.getFrameStartIndex();
        int length = result.getFrameLength();
        int inReaderIndex = in.readerIndex();
        int inReadableBytes = in.readableBytes();
        return startIndex + length <= inReaderIndex + inReadableBytes;
    }

    /**
     * 检测到合法的帧报文，去头、取帧、留尾
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     * @param out
     *            the List to which decoded messages should be added
     */
    private void doDetectSuccess(ChannelHandlerContext ctx, ByteBuf in, List<Object> out, IotDetectionResult result) {
        int startIndex = result.getFrameStartIndex();
        int length = result.getFrameLength();
        String cmdRespFrameHex = ByteBufUtil.hexDump(in);
        // 去头
        if (startIndex > 0) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[04]帧检测：删除报文帧前无效字节, 原始报文: 0x{}, 检测结果: {}, 帧前无效报文: 0x{}", cmdRespFrameHex,
                        JsonUtils.writeValueAsString(result),
                        ByteBufUtil.hexDump(in, in.readerIndex(), startIndex - in.readerIndex()));
            }
            in.skipBytes(startIndex);
        }
        // 取帧
        ByteBuf frameBuf = in.copy(in.readerIndex(), length);
        out.add(frameBuf);
        in.skipBytes(length);
        // 留尾，继续帧检测或等待后续报文进行合包
        boolean needContinueDetect = in.isReadable();
        if (needContinueDetect) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[04]帧检测：识别出粘包报文, 原始报文: 0x{}，检测结果: {}, 帧检测结果报文: 0x{}, 剩余报文: 0x{}",
                        cmdRespFrameHex, JsonUtils.writeValueAsString(result), ByteBufUtil.hexDump(frameBuf),
                        ByteBufUtil.hexDump(in, in.readerIndex(), in.readableBytes()));
            }
            in.discardReadBytes();
        }
        Cmd.setCmdStatus(ctx, IotCmdStatus.DETECT_SUCCESS);
    }

    /**
     * 不是合法的帧报文，直接抛弃
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param in
     *            the ByteBuf from which to read data
     */
    private void doDetectError(ChannelHandlerContext ctx, ByteBuf in, IotDetectionResult result) {
        // 发送事件
        ctx.fireUserEventTriggered(IotDetectionErrorEvent.error(Auth.getGatewaySn(ctx), result.getDetectionCode(),
                "帧检测失败，抛弃所有报文！原始报文: 0x" + ByteBufUtil.hexDump(in)));
        // 消费掉全部字节，等效于抛弃所有字节
        in.skipBytes(in.readableBytes());
    }

    /**
     * 如果累积器中有数据，则清空累积器中的所有数据
     */
    private void clearDecodeCumulator(ChannelHandlerContext ctx) {
        ByteBuf cumulation = internalBuffer();
        if (cumulation.isReadable()) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[04]帧检测：清空累积数据！累积报文: 0x{}", ByteBufUtil.hexDump(cumulation));
            }
            cumulation.clear();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IotClearDecodeCumulatorEvent) {
            clearDecodeCumulator(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 帧检测结果结构体
     */
    static class FrameDetectResult {
        /**
         * 帧检测结束，无需执行后面的检测项
         */
        boolean detectFinished = false;

        static FrameDetectResult getInstance(boolean detectFinished) {
            FrameDetectResult instance = new FrameDetectResult();
            instance.detectFinished = detectFinished;
            return instance;
        }
    }
}
