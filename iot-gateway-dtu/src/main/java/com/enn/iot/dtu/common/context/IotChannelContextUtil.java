package com.enn.iot.dtu.common.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import org.apache.commons.lang3.StringUtils;

import com.enn.iot.dtu.common.devicecmdstatus.dto.DeviceCmdHealthStatus;
import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.common.event.IotClearDecodeCumulatorEvent;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * 指令队列工具类
 **/
@Slf4j
public class IotChannelContextUtil {
    private static final String KEY_AUTH_AUTHED = "Auth.authed";
    private static final String KEY_AUTH_GATEWAY_SN = "Auth.gatewaySn";
    private static final String KEY_CONNECTION_CONNECT_TIME = "Connect.connectTime";
    private static final String KEY_CONNECTION_DISCONNECT_TIME = "Connect.disconnectTime";
    private static final String KEY_CMD_CURRENT_CMD = "Cmd.current.cmd";
    private static final String KEY_CMD_CURRENT_CMD_STATUS = "Cmd.current.cmd.status";
    private static final String KEY_CMD_CURRENT_CMD_REQUEST_FRAME_HEX = "Cmd.current.cmd.frame.hex";
    private static final String KEY_CMD_CURRENT_CMD_ENCODER_HEX = "Cmd.current.cmd.encoder.hex";
    private static final String KEY_CMD_CURRENT_CMD_DECODER_HEX = "Cmd.current.cmd.decoder.hex";
    private static final String KEY_CMD_CURRENT_CMD_IS_QUEUE_LAST = "Cmd.current.is.queue.last";
    private static final String KEY_CMD_PREVIOUS_CMD = "Cmd.previous.cmd";
    private static final String KEY_CMD_PREVIOUS_CMD_STATUS = "Cmd.previous.cmd.status";
    private static final String KEY_CMD_FIRST_CMD_SEND_TIME = "Cmd.fist.cmd.send.time";
    private static final String KEY_CMD_LAST_CMD_RECEIVE_TIME = "Cmd.last.cmd.receive.time";
    private static final String KEY_CMD_SCHEDULED_FUTURE = "Cmd.scheduled.future";
    private static final String KEY_DEVICE_AND_CMD_HEALTH_STATUS = "Device.and.cmd.health.status";
    private static final String KEY_DEVICE_HEALTH_STATUS_LOG = "Device.health.status.log";
    private static final String KEY_CMD_CURRENT_CMD_NUMBER = "Cmd.current.cmd.number";
    private static final String KEY_CMD_CURRENT_WRITE_CMD_SLEEP_STATUS = "Cmd.current.write.cmd.sleep.status";
    private static final String KEY_WRITE_CMD_CURRENT_MAIN_DATA = "Cmd.current.write.cmd.main.data";

    private IotChannelContextUtil() {}

    public static class Log {
        public static String context(ChannelHandlerContext ctx) {
            return context(Auth.getGatewaySn(ctx), ctx.channel().id().asShortText());
        }

        public static String context(String gatewaySn) {
            return context(gatewaySn, null);
        }

        public static String context(ChannelHandlerContext ctx, String gatewaySn) {
            if (ctx == null) {
                return context(gatewaySn, null);
            } else {
                return context(gatewaySn, ctx.channel().id().asShortText());
            }
        }

        private static String context(String gatewaySn, String channelId) {
            if (channelId == null) {
                return "[" + gatewaySn + "]";
            }
            StringBuilder result = new StringBuilder();
            result.append("[0x").append(channelId).append("] ");
            result.append("[");
            if (gatewaySn != null) {
                result.append(gatewaySn);
            }
            result.append("] ");
            return result.toString();
        }
    }

    /**
     * 指令相关上下文
     */
    public static class Cmd {

        private Cmd() {}

        /**
         * 如果解码的积累器有数据，清空积累器中的数据
         *
         * @param ctx
         *            ChannelHandlerContext
         */
        public static void clearDecodeCumulator(ChannelHandlerContext ctx) {
            ctx.pipeline().firstContext().fireUserEventTriggered(IotClearDecodeCumulatorEvent.instance());
        }

        public static boolean hasExecutingCmd(ChannelHandlerContext ctx) {
            return hasExecutingCmd(ctx.channel());
        }

        static boolean hasExecutingCmd(AttributeMap attrMap) {
            IotCmdStatus status = getCmdStatus(attrMap);
            boolean hasExecutingCmd = false;
            if (status != null) {
                if (status == IotCmdStatus.READY_FOR_SENDING || status == IotCmdStatus.SENT_AND_WAITING_RESPONSE) {
                    hasExecutingCmd = true;
                }
            }
            return hasExecutingCmd;
        }

        /**
         * 查询调度任务句柄
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static ScheduledFuture<?> getScheduledFuture(ChannelHandlerContext ctx) {
            return getScheduledFuture(ctx.channel());
        }

        static ScheduledFuture<?> getScheduledFuture(AttributeMap attrMap) {
            ScheduledFuture<?> result = getScheduledFutureWithoutLog(attrMap);
            if (log.isTraceEnabled()) {
                log.trace(Log.context(((Channel)attrMap).pipeline().lastContext()) + "getScheduledFuture: {}",
                        result == null ? null : result.hashCode());
            }
            return result;
        }

        static ScheduledFuture<?> getScheduledFutureWithoutLog(AttributeMap attrMap) {
            return (ScheduledFuture<?>)attrMap.attr(AttributeKey.valueOf(KEY_CMD_SCHEDULED_FUTURE)).get();
        }

        /**
         * 删除保存的异步任务的句柄
         *
         * @param ctx
         *            ChannelHandlerContext
         */
        public static void clearScheduledFuture(ChannelHandlerContext ctx) {
            setScheduledFuture(ctx.channel(), null);
        }

        /**
         * 保存异步任务的句柄
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param future
         */
        public static void setScheduledFuture(ChannelHandlerContext ctx, ScheduledFuture<?> future) {
            setScheduledFuture(ctx.channel(), future);
        }

        static void setScheduledFuture(AttributeMap attrMap, ScheduledFuture<?> future) {
            if (log.isTraceEnabled()) {
                ScheduledFuture<?> oldFuture = getScheduledFutureWithoutLog(attrMap);
                log.trace(Log.context(((Channel)attrMap).pipeline().lastContext()) + "setScheduledFuture: {}，修改前: {}",
                        future == null ? null : future.hashCode(), oldFuture == null ? null : oldFuture.hashCode());
            }
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_SCHEDULED_FUTURE)).set(future);
        }

        /**
         * 查询当前指令的状态，当前指令指的是准备执行的或正在执行的或刚执行结束的指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static IotCmdStatus getCmdStatus(ChannelHandlerContext ctx) {
            return getCmdStatus(ctx.channel());
        }

        static IotCmdStatus getCmdStatus(AttributeMap attrMap) {
            IotCmdStatus status = getCmdStatusWithoutLog(attrMap);
            if (log.isTraceEnabled()) {
                log.trace(Log.context(((Channel)attrMap).pipeline().lastContext()) + "getCmdStatus: {}", status);
            }
            return status;
        }

        static IotCmdStatus getCmdStatusWithoutLog(AttributeMap attrMap) {
            IotCmdStatus status = (IotCmdStatus)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_STATUS)).get();
            return status != null ? status : IotCmdStatus.IDLE_WITHOUT_COMMAND;
        }

        /**
         * 设置当前指令的状态，当前指令指的是准备执行的或正在执行的或刚执行结束的指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param status
         */
        public static void setCmdStatus(ChannelHandlerContext ctx, IotCmdStatus status) {
            setCmdStatus(ctx.channel(), status);
        }

        static void setCmdStatus(AttributeMap attrMap, IotCmdStatus status) {
            if (log.isTraceEnabled()) {
                log.trace(Log.context(((Channel)attrMap).pipeline().lastContext()) + "setCmdStatus: {}, 修改前: {}",
                        status, getCmdStatusWithoutLog(attrMap));
            }
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_STATUS)).set(status);
        }

        /**
         * 查询当前指令是否为读指令列表最后一条指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static boolean getCurrentCmdIsQueueLast(ChannelHandlerContext ctx) {
            return getCurrentCmdIsQueueLast(ctx.channel());
        }

        static boolean getCurrentCmdIsQueueLast(AttributeMap attrMap) {
            Boolean isLast = (Boolean)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_IS_QUEUE_LAST)).get();
            return isLast != null && isLast;
        }

        /**
         * 设置当前指令是否为读指令列表最后一条指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param isLast
         */
        public static void setCurrentCmdIsQueueLast(ChannelHandlerContext ctx, boolean isLast) {
            setCurrentCmdIsQueueLast(ctx.channel(), isLast);
        }

        static void setCurrentCmdIsQueueLast(AttributeMap attrMap, boolean isLast) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_IS_QUEUE_LAST)).set(isLast);
        }

        /**
         * 当前指令：指的是准备执行的，或正在执行的，或刚执行结束的指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static AbstractIotCmdReq getCurrentCmd(ChannelHandlerContext ctx) {
            return getCurrentCmd(ctx.channel());
        }

        static AbstractIotCmdReq getCurrentCmd(AttributeMap attrMap) {
            return (AbstractIotCmdReq)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD)).get();
        }

        /**
         * 当前指令：指的是准备执行的，或正在执行的，或刚执行结束的指令。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param cmdReq
         * @param status
         */
        public static void setCurrentCmdWithStatus(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq,
                                                   IotCmdStatus status) {
            setCurrentCmdWithStatus(ctx.channel(), cmdReq, status);
        }

        static void setCurrentCmdWithStatus(AttributeMap attrMap, AbstractIotCmdReq cmdReq, IotCmdStatus status) {
            setPreviousCmd(attrMap, getCurrentCmd(attrMap));
            setPreviousCmdStatus(attrMap, getCmdStatus(attrMap));
            setCurrentCmd(attrMap, cmdReq);
            setCmdStatus(attrMap, status);
            setCurrentCmdReqFrameHex(attrMap, null);
        }

        static void setCurrentCmd(AttributeMap attrMap, AbstractIotCmdReq cmdReq) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD)).set(cmdReq);
        }

        public static void setCurrentCmd(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq) {
            setCurrentCmd(ctx.channel(), cmdReq);
        }

        public static String getCurrentCmdReqFrameHex(ChannelHandlerContext ctx) {
            return getCurrentCmdReqFrameHex(ctx.channel());
        }

        static String getCurrentCmdReqFrameHex(AttributeMap attrMap) {
            return (String)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_REQUEST_FRAME_HEX)).get();
        }

        public static void setCurrentCmdReqFrameHex(ChannelHandlerContext ctx, String cmdReqFrameHex) {
            setCurrentCmdReqFrameHex(ctx.channel(), cmdReqFrameHex);
            setCurrentCmdReqEncoderHex(ctx.channel(), cmdReqFrameHex);
        }

        static void setCurrentCmdReqFrameHex(AttributeMap attrMap, String cmdReqFrameHex) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_REQUEST_FRAME_HEX)).set(cmdReqFrameHex);
        }

        static void setCurrentCmdReqEncoderHex(AttributeMap attrMap, String cmdReqFrameHex) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_ENCODER_HEX)).set(cmdReqFrameHex);
        }

        static void setCurrentCmdReqDecoderHex(AttributeMap attrMap, String cmdReqFrameHex) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_DECODER_HEX)).set(cmdReqFrameHex);
        }

        public static void setCurrentCmdDecoderHex(ChannelHandlerContext ctx, String cmdReqFrameHex) {
            setCurrentCmdReqDecoderHex(ctx.channel(), cmdReqFrameHex);
        }

        static String getCurrentCmdReqEncoderHex(AttributeMap attrMap) {
            return (String)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_ENCODER_HEX)).get();
        }

        static String getCurrentCmdReqDecoderHex(AttributeMap attrMap) {
            return (String)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_DECODER_HEX)).get();
        }

        public static String getCurrentCmdReqDecoderHex(ChannelHandlerContext ctx) {
            return getCurrentCmdReqDecoderHex(ctx.channel());
        }

        public static String getCurrentCmdReqEncoderHex(ChannelHandlerContext ctx) {
            return getCurrentCmdReqEncoderHex(ctx.channel());
        }

        /**
         * 获取上一条指令
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static AbstractIotCmdReq getPreviousCmd(ChannelHandlerContext ctx) {
            return getPreviousCmd(ctx.channel());
        }

        static AbstractIotCmdReq getPreviousCmd(AttributeMap attrMap) {
            return (AbstractIotCmdReq)attrMap.attr(AttributeKey.valueOf(KEY_CMD_PREVIOUS_CMD)).get();
        }

        static void setPreviousCmd(AttributeMap attrMap, AbstractIotCmdReq cmdReq) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_PREVIOUS_CMD)).set(cmdReq);
        }

        public static IotCmdStatus getPreviousCmdStatus(ChannelHandlerContext ctx) {
            return getPreviousCmdStatus(ctx.channel());
        }

        static IotCmdStatus getPreviousCmdStatus(AttributeMap attrMap) {
            return (IotCmdStatus)attrMap.attr(AttributeKey.valueOf(KEY_CMD_PREVIOUS_CMD_STATUS)).get();
        }

        static void setPreviousCmdStatus(AttributeMap attrMap, IotCmdStatus cmdStatus) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_PREVIOUS_CMD_STATUS)).set(cmdStatus);
        }

        /**
         * 读指令队列第一条指令开始执行的时间
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param timeMs
         */
        public static void setQueueFirstCmdSendTimeMs(ChannelHandlerContext ctx, Long timeMs) {
            setQueueFirstCmdSendTimeMs(ctx.channel(), timeMs);
            setQueueLastCmdReceiveTimeMs(ctx.channel(), null);
        }

        static void setQueueFirstCmdSendTimeMs(AttributeMap attrMap, Long timeMs) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_FIRST_CMD_SEND_TIME)).set(timeMs);
        }

        /**
         * 读指令队列第一条指令开始执行的时间
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static Long getQueueFirstCmdSendTimeMs(ChannelHandlerContext ctx) {
            return getQueueFirstCmdSendTimeMs(ctx.channel());
        }

        static Long getQueueFirstCmdSendTimeMs(AttributeMap attrMap) {
            return (Long)attrMap.attr(AttributeKey.valueOf(KEY_CMD_FIRST_CMD_SEND_TIME)).get();
        }

        /**
         * 读指令队列最后一个指令执行结束时间。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @param timeMs
         */
        public static void setQueueLastCmdReceiveTimeMs(ChannelHandlerContext ctx, Long timeMs) {
            setQueueLastCmdReceiveTimeMs(ctx.channel(), timeMs);
        }

        static void setQueueLastCmdReceiveTimeMs(AttributeMap attrMap, Long timeMs) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_LAST_CMD_RECEIVE_TIME)).set(timeMs);
        }

        /**
         * 读指令队列最后一个指令执行结束时间。
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static Long getQueueLastCmdReceiveTimeMs(ChannelHandlerContext ctx) {
            return getQueueLastCmdReceiveTimeMs(ctx.channel());
        }

        static Long getQueueLastCmdReceiveTimeMs(AttributeMap attrMap) {
            return (Long)attrMap.attr(AttributeKey.valueOf(KEY_CMD_LAST_CMD_RECEIVE_TIME)).get();
        }

        /**
         * 当前指令是第几条
         *
         * @param ctx
         *            ChannelHandlerContext
         * @return
         */
        public static void setCurrentCmdNumber(ChannelHandlerContext ctx, int number) {
            setCurrentCmdNumber(ctx.channel(), number);
        }

        public static void setCurrentWriteCmdSleepStatus(ChannelHandlerContext ctx, boolean sleepStatus) {
            setCurrentWriteCmdSleepStatus(ctx.channel(), sleepStatus);
        }

        public static void setCurrentWriteCmdSleepStatus(AttributeMap attrMap, boolean sleepStatus) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_WRITE_CMD_SLEEP_STATUS)).set(sleepStatus);
        }

        public static boolean getCurrentWriteCmdSleepStatus(ChannelHandlerContext ctx) {
            return getCurrentWriteCmdSleepStatus(ctx.channel());
        }

        private static boolean getCurrentWriteCmdSleepStatus(AttributeMap attrMap) {
            Object obj = attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_WRITE_CMD_SLEEP_STATUS)).get();
            if (obj == null) {
                return false;
            }
            return (boolean)obj;
        }

        static void setCurrentCmdNumber(AttributeMap attrMap, int number) {
            attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_NUMBER)).set(number);
        }

        public static int getCurrentCmdNumber(ChannelHandlerContext ctx) {
            return getCurrentCmdNumber(ctx.channel());
        }

        static int getCurrentCmdNumber(AttributeMap attrMap) {
            return (int)attrMap.attr(AttributeKey.valueOf(KEY_CMD_CURRENT_CMD_NUMBER)).get();
        }

        public static void setCurrentWriteCmdMainData(ChannelHandlerContext ctx, ControlCmdDTO controlCmdDTO) {
            ctx.channel().attr(AttributeKey.valueOf(KEY_WRITE_CMD_CURRENT_MAIN_DATA)).set(controlCmdDTO);
        }

        public static ControlCmdDTO getCurrentWriteCmdMainData(ChannelHandlerContext ctx) {
            return getCurrentWriteCmdMainData(ctx.channel());
        }

        static ControlCmdDTO getCurrentWriteCmdMainData(AttributeMap attrMap) {
            return (ControlCmdDTO)attrMap.attr(AttributeKey.valueOf(KEY_WRITE_CMD_CURRENT_MAIN_DATA)).get();
        }
    }

    /**
     * 读指令上下文
     */
    public static class ReadCmd {

        private ReadCmd() {}

        /**
         * Retrieves, but does not remove, the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq peekQueue(ChannelHandlerContext ctx) {
            return peekQueue(ctx.channel());
        }

        static AbstractIotCmdReq peekQueue(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return null;
            }
            return IotGlobalContextUtil.ReadCmd.peekQueue(gatewaySn);
        }

        /**
         * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq pollQueue(ChannelHandlerContext ctx) {
            return pollQueue(ctx.channel());
        }

        static AbstractIotCmdReq pollQueue(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return null;
            }
            return IotGlobalContextUtil.ReadCmd.pollQueue(gatewaySn);
        }

        public static int getQueueSize(ChannelHandlerContext ctx) {
            return getQueueSize(ctx.channel());
        }

        static int getQueueSize(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return 0;
            }
            return IotGlobalContextUtil.ReadCmd.getQueueSize(gatewaySn);
        }

        public static int getListSize(ChannelHandlerContext ctx) {
            return getListSize(ctx.channel());
        }

        static int getListSize(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return 0;
            }
            return IotGlobalContextUtil.ReadCmd.getListSize(gatewaySn);
        }

        public static int rebuildQueue(ChannelHandlerContext ctx) {
            return rebuildQueue(ctx.channel());
        }

        static int rebuildQueue(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return 0;
            }
            return IotGlobalContextUtil.ReadCmd.rebuildQueue(gatewaySn);
        }

        public static void addOneToFront(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (gatewaySn == null) {
                return;
            }

            IotGlobalContextUtil.ReadCmd.addOneToFront(gatewaySn, cmdReq);
        }
    }

    /**
     * 写指令相关上下文
     */
    public static class WriteCmd {

        /**
         * Retrieves, but does not remove, the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq peekQueue(ChannelHandlerContext ctx) {
            return peekQueue(ctx.channel());
        }

        static AbstractIotCmdReq peekQueue(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return null;
            }
            return IotGlobalContextUtil.WriteCmd.peekQueue(gatewaySn);
        }

        /**
         * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq pollQueue(ChannelHandlerContext ctx) {
            return pollQueue(ctx.channel());
        }

        static AbstractIotCmdReq pollQueue(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return null;
            }
            return IotGlobalContextUtil.WriteCmd.pollQueue(gatewaySn);
        }

        public static int getQueueSize(ChannelHandlerContext ctx) {
            return getQueueSize(ctx.channel());
        }

        static int getQueueSize(AttributeMap attrMap) {
            String gatewaySn = Auth.getGatewaySn(attrMap);
            if (gatewaySn == null) {
                return 0;
            }
            return IotGlobalContextUtil.WriteCmd.getQueueSize(gatewaySn);
        }

        public static void addOneToFront(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (gatewaySn == null) {
                return;
            }

            IotGlobalContextUtil.WriteCmd.addOneToFront(gatewaySn, cmdReq);
        }
    }

    /**
     * 认证相关上下文
     */
    public static class Auth {

        private Auth() {}

        public static void setAuthed(ChannelHandlerContext ctx, String gatewaySn) {
            setAuthed(ctx.channel(), gatewaySn);
        }

        static void setAuthed(AttributeMap attrMap, String gatewaySn) {
            attrMap.attr(AttributeKey.valueOf(KEY_AUTH_GATEWAY_SN)).set(gatewaySn);
            attrMap.attr(AttributeKey.valueOf(KEY_AUTH_AUTHED)).set(true);
        }

        public static boolean hasAuthed(ChannelHandlerContext ctx) {
            return hasAuthed(ctx.channel());
        }

        static boolean hasAuthed(AttributeMap attrMap) {
            Boolean authed = (Boolean)attrMap.attr(AttributeKey.valueOf(KEY_AUTH_AUTHED)).get();
            return authed != null && authed;
        }

        public static String getGatewaySn(ChannelHandlerContext ctx) {
            return getGatewaySn(ctx.channel());
        }

        static String getGatewaySn(AttributeMap attrMap) {
            return (String)attrMap.attr(AttributeKey.valueOf(KEY_AUTH_GATEWAY_SN)).get();
        }
    }

    /**
     * 主数据相关上下文
     */
    public static class MainData {

        private MainData() {}

        public static MainDataDTO getMainData(ChannelHandlerContext ctx) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (null == gatewaySn) {
                return null;
            }
            return IotGlobalContextUtil.MainData.getMainData(gatewaySn);
        }
    }

    /**
     * 连接相关上下文
     */
    public static class Connection {
        public static void setConnectedTimeMs(ChannelHandlerContext ctx, Long timeMs) {
            setConnectedTimeMs(ctx.channel(), timeMs);
        }

        static void setConnectedTimeMs(AttributeMap attrMap, Long timeMs) {
            attrMap.attr(AttributeKey.valueOf(KEY_CONNECTION_CONNECT_TIME)).set(timeMs);
        }

        public static Long getConnectedTimeMs(ChannelHandlerContext ctx) {
            return getConnectedTimeMs(ctx.channel());
        }

        static Long getConnectedTimeMs(AttributeMap attrMap) {
            return (Long)attrMap.attr(AttributeKey.valueOf(KEY_CONNECTION_CONNECT_TIME)).get();
        }

        public static void setDisconnectedTimeMs(ChannelHandlerContext ctx, Long timeMs) {
            setDisconnectedTimeMs(ctx.channel(), timeMs);
        }

        static void setDisconnectedTimeMs(AttributeMap attrMap, Long timeMs) {
            attrMap.attr(AttributeKey.valueOf(KEY_CONNECTION_DISCONNECT_TIME)).set(timeMs);
        }

        public static Long getDisconnectedTimeMs(ChannelHandlerContext ctx) {
            return getDisconnectedTimeMs(ctx.channel());
        }

        static Long getDisconnectedTimeMs(AttributeMap attrMap) {
            return (Long)attrMap.attr(AttributeKey.valueOf(KEY_CONNECTION_DISCONNECT_TIME)).get();
        }
    }

    public static class DeviceCmdStatus {

        public static List<DeviceCmdHealthStatus> getDeviceCmdHealthStatusLst(ChannelHandlerContext ctx) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (null == gatewaySn) {
                return null;
            }
            return getDeviceCmdHealthStatusLst(ctx.channel());
        }

        static List<DeviceCmdHealthStatus> getDeviceCmdHealthStatusLst(AttributeMap attributeMap) {
            return (List<DeviceCmdHealthStatus>)attributeMap
                    .attr(AttributeKey.valueOf(KEY_DEVICE_AND_CMD_HEALTH_STATUS)).get();
        }

        public static DeviceCmdHealthStatus getDeviceCmdHealthStatus(ChannelHandlerContext ctx, String stationId,
                                                                     String trdPtyCode) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (null == gatewaySn) {
                return null;
            }

            return getDeviceCmdHealthStatus(ctx.channel(), stationId, trdPtyCode);
        }

        static DeviceCmdHealthStatus getDeviceCmdHealthStatus(AttributeMap attributeMap, String stationId,
                                                              String trdPtyCode) {
            List<DeviceCmdHealthStatus> list = getDeviceCmdHealthStatusLst(attributeMap);
            if (list == null || list.isEmpty() || stationId == null || trdPtyCode == null) {
                return null;
            }
            List<DeviceCmdHealthStatus> resultLst = list.stream()
                    .filter(status -> status.getStationId().equals(stationId) && status.getTrdPtyCode().equals(trdPtyCode))
                    .collect(Collectors.toList());
            if (resultLst.isEmpty()) {
                return null;
            }
            return resultLst.get(0);
        }

        public static void setDeviceCmdHealthStatus(ChannelHandlerContext ctx, DeviceCmdHealthStatus status) {
            String gatewaySn = Auth.getGatewaySn(ctx);
            if (null == gatewaySn) {
                return;
            }
            DeviceCmdStatus.setDeviceCmdHealthStatus(ctx.channel(), status);
        }

        static void setDeviceCmdHealthStatus(AttributeMap attributeMap, DeviceCmdHealthStatus status) {
            List<DeviceCmdHealthStatus> list = getDeviceCmdHealthStatusLst(attributeMap);
            if (list == null) {
                list = new ArrayList<>();
                list.add(status);
            } else {
                DeviceCmdHealthStatus oldStatus =
                        getDeviceCmdHealthStatus(attributeMap, status.getStationId(), status.getTrdPtyCode());
                if (oldStatus != null) {
                    list.remove(oldStatus);
                }
                list.add(status);
            }
            attributeMap.attr(AttributeKey.valueOf(KEY_DEVICE_AND_CMD_HEALTH_STATUS)).set(list);
        }

        public static void resetDeviceCmdStatus(ChannelHandlerContext ctx) {
            List<DeviceCmdHealthStatus> lst = getDeviceCmdHealthStatusLst(ctx);
            if (lst == null || lst.isEmpty()) {
                return;
            }

            for (DeviceCmdHealthStatus deviceCmdHealthStatus : lst) {
                deviceCmdHealthStatus.setHasCmdHealth(false);
            }
            setDeviceCmdHealthStatusLst(ctx.channel(), lst);
        }

        static void setDeviceCmdHealthStatusLst(AttributeMap attributeMap, List<DeviceCmdHealthStatus> lst) {
            attributeMap.attr(AttributeKey.valueOf(KEY_DEVICE_AND_CMD_HEALTH_STATUS)).set(lst);
        }

        public static String getDeviceStatusLog(ChannelHandlerContext ctx) {
            return getDeviceStatusLog(ctx.channel());
        }

        static String getDeviceStatusLog(AttributeMap attributeMap) {
            return (String)attributeMap.attr(AttributeKey.valueOf(KEY_DEVICE_HEALTH_STATUS_LOG)).get();
        }

        public static void setDeviceStatusLog(ChannelHandlerContext ctx, String logInfo) {
            setDeviceStatusLog(ctx.channel(), logInfo);
        }

        static void setDeviceStatusLog(AttributeMap attributeMap, String logInfo) {
            String oldLogInfo = getDeviceStatusLog(attributeMap);
            StringBuilder builder = new StringBuilder();
            if (!StringUtils.isEmpty(oldLogInfo)) {
                builder.append(oldLogInfo);
            }
            builder.append(logInfo);
            attributeMap.attr(AttributeKey.valueOf(KEY_DEVICE_HEALTH_STATUS_LOG)).set(builder.toString());
        }

        public static void resetDeviceStatusLog(ChannelHandlerContext ctx) {
            AttributeMap attributeMap = ctx.channel();
            attributeMap.attr(AttributeKey.valueOf(KEY_DEVICE_HEALTH_STATUS_LOG)).set(null);
        }
    }
}
