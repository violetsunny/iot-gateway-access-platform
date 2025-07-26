package com.enn.iot.dtu.service.impl;


import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.devicecmdstatus.dto.DeviceCmdHealthStatus;
import com.enn.iot.dtu.common.enums.IotCmdStatus;
import com.enn.iot.dtu.common.properties.IotProperties;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.service.CmdExecuteService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CmdExecuteServiceImpl implements CmdExecuteService {

    /**
     * 指令执行最小时间间隔，毫秒
     */
    private static final long MIN_INTERVAL_BETWEEN_COMMANDS_MS = 1;
    /**
     * 采集周期，两轮采集的轮询间隔时间，单位毫秒
     */
    private final long collectPollingIntervalMs;

    private final int retryCount;

    public CmdExecuteServiceImpl(IotProperties iotProperties) {
        this.collectPollingIntervalMs = iotProperties.getCollectPollingInterval().toMillis();
        this.retryCount = iotProperties.getRetryCount();
    }

    /**
     * DTU 配置更新后，需要尝试执行下一条指令。<br/>
     * 举例：<br/>
     * 1阶段：DTU 生成读指令列表为空，指令执行状态也是空闲。<br/>
     * 2阶段：配置更新后，生成了新的读指令，这时是需要调用此方法触发第一次指令的执行。<br/>
     *
     * @param gatewaySn
     */
    @Override
    public void executeNextCmdIf(String gatewaySn) {
        Channel channel = IotGlobalContextUtil.Channels.getChannel(gatewaySn);
        if (channel == null) {
            if (log.isWarnEnabled()) {
                log.warn(IotChannelContextUtil.Log.context(gatewaySn) + "[exec] 执行指令失败，DTU没有连接或已断开连接！");
            }
            return;
        }
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        IotCmdStatus status = IotChannelContextUtil.Cmd.getCmdStatus(ctx);
        if (status == null || status == IotCmdStatus.IDLE_WITHOUT_COMMAND) {
            // 如果当前处于空闲状态，尝试执行指令。
            executeNextCmdIf(ctx);
        }
    }

    @Override
    public void executeNextCmdIf(ChannelHandlerContext ctx) {
        if (!checkAllowExecuteCmd(ctx)) {
            return;
        }
        // 更新读指令队列执行结束时间。
        if (IotChannelContextUtil.Cmd.getCurrentCmdIsQueueLast(ctx)) {
            IotChannelContextUtil.Cmd.setQueueLastCmdReceiveTimeMs(ctx, System.currentTimeMillis());
        }
        // 计算下个指令的查询对象
        NextCmdQuery nextCmdQuery = calculateNextCmdQuery(ctx);
        if (!nextCmdQuery.hasNextCmd) {
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.IDLE_WITHOUT_COMMAND);
            if (log.isWarnEnabled()) {
                log.warn(IotChannelContextUtil.Log.context(ctx) + "[exec] 没有需要执行的指令！");
            }
            return;
        }
        boolean needSleep = false;
        ReadQueueResult queueResult = null;
        if (nextCmdQuery.readQueueHasRebuilt) {
            queueResult = calculateReadQueueResult(ctx);
            IotChannelContextUtil.DeviceCmdStatus.resetDeviceCmdStatus(ctx);
            needSleep = queueResult.needSleep;
            if (queueResult.isFinished) {
                if (queueResult.isTimeOver) {
                    if (log.isWarnEnabled()) {
                        log.warn(
                                IotChannelContextUtil.Log.context(ctx)
                                        + "[exec] 读指令队列执行结束，本轮耗时超过采集周期！本轮耗时: {}ms, 采集周期: {}ms, 读指令列表长度: {}",
                                queueResult.costMs, collectPollingIntervalMs,
                                IotChannelContextUtil.ReadCmd.getListSize(ctx));
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(
                                IotChannelContextUtil.Log.context(ctx)
                                        + "[exec] 读指令队列执行结束，本轮耗时: {}ms, 采集周期: {}ms, 距离下轮开始时间: {}ms",
                                queueResult.costMs, collectPollingIntervalMs, queueResult.nextDelayMs);
                    }
                }
            }
            // 重置本轮采集从站设备的状态变化
            String deviceStatusLog = IotChannelContextUtil.DeviceCmdStatus.getDeviceStatusLog(ctx);
            if (!StringUtils.isEmpty(deviceStatusLog)) {
                if (log.isTraceEnabled()) {
                    log.trace(IotChannelContextUtil.Log.context(ctx) + "[exec] 读指令队列执行结束，本轮从站设备通讯状态变化信息：{}",
                            deviceStatusLog);
                }
            }
            IotChannelContextUtil.DeviceCmdStatus.resetDeviceStatusLog(ctx);
        }
        if (needSleep) {
            doSleep4NextQueue(ctx, queueResult);
        } else {
            doExecuteNextCmdReq(ctx, nextCmdQuery);
        }
    }

    @Override
    public void retryCmd(ChannelHandlerContext ctx) {
        if (!checkAllowExecuteCmd(ctx)) {
            return;
        }

        AbstractIotCmdReq currentCmd = IotChannelContextUtil.Cmd.getCurrentCmd(ctx);
        int hasRetryCount = currentCmd.getHasRetryCount();
        //已经重试了N次，判断当前设备状态
        if (hasRetryCount == retryCount) {
            DeviceCmdHealthStatus curDeviceCmdStatus = IotChannelContextUtil.DeviceCmdStatus
                    .getDeviceCmdHealthStatus(ctx,
                            currentCmd.getStationId(), currentCmd.getTrdPtyCode());

            if (curDeviceCmdStatus == null) {
                curDeviceCmdStatus = new DeviceCmdHealthStatus();
                curDeviceCmdStatus.setStationId(currentCmd.getStationId());
                curDeviceCmdStatus.setTrdPtyCode(currentCmd.getTrdPtyCode());
            }

            NextCmdQuery nextCmdQuery = calculateNextCmdQuery(ctx);
            // 当前从站设备状态异常，将移除当前从站设备的指令
            if (!curDeviceCmdStatus.isDeviceHealth()) {
                if (nextCmdQuery.hasNextCmd) {
                    if (!nextCmdQuery.readQueueHasRebuilt) {
                        // 移除当前从站设备的指令
                        while (true) {
                            AbstractIotCmdReq nextReq = IotChannelContextUtil.ReadCmd.peekQueue(ctx);
                            if (nextReq == null) {
                                break;
                            } else {
                                if (nextReq.getStationId().equals(currentCmd.getStationId())
                                        && nextReq.getTrdPtyCode().equals(currentCmd.getTrdPtyCode())) {
                                    IotChannelContextUtil.ReadCmd.pollQueue(ctx);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                // 判断当前命令是否为该设备的最后一条命令
                if (nextCmdQuery.hasNextCmd) {
                    boolean isLastSameDeviceCmd = false;
                    if (nextCmdQuery.readQueueHasRebuilt) {
                        isLastSameDeviceCmd = true;
                    } else {
                        if (currentCmd.getReadonly()) {
                            AbstractIotCmdReq nextReq = IotChannelContextUtil.ReadCmd.peekQueue(ctx);
                            // 是当前从站设备最后一条指令
                            if (!nextReq.getStationId().equals(currentCmd.getStationId())
                                    || !nextReq.getTrdPtyCode().equals(currentCmd.getTrdPtyCode())) {
                                isLastSameDeviceCmd = true;
                            }
                        }
                    }

                    if (isLastSameDeviceCmd) {
                        // 判断当前从站设备的所有命令是否都执行失败
                        if (!curDeviceCmdStatus.isHasCmdHealth()) {
                            curDeviceCmdStatus.setDeviceHealth(false);
                            curDeviceCmdStatus.setHasCmdHealth(false);
                            IotChannelContextUtil.DeviceCmdStatus.setDeviceCmdHealthStatus(ctx, curDeviceCmdStatus);
                            if (log.isWarnEnabled()) {
                                log.warn(
                                        IotChannelContextUtil.Log.context(ctx)
                                                + "[exec]子设备通讯熔断，子设备下所有指令全部执行失败。子设备系统编码:{}，子设备三方标识：{}",
                                        currentCmd.getStationId(), currentCmd.getTrdPtyCode());
                            }
                            String logInfo = String.format("%s从站设备stationId:%s, trdPtyCode:%s,通讯正常-->异常！",
                                    IotChannelContextUtil.Log.context(ctx), currentCmd.getStationId(),
                                    currentCmd.getTrdPtyCode());
                            IotChannelContextUtil.DeviceCmdStatus.setDeviceStatusLog(ctx, logInfo);
                        }
                    } else {
                        curDeviceCmdStatus.setHasCmdHealth(curDeviceCmdStatus.isHasCmdHealth() || false);
                        IotChannelContextUtil.DeviceCmdStatus.setDeviceCmdHealthStatus(ctx, curDeviceCmdStatus);
                    }
                }
            }
        } else {
            // 拷贝当前命令，将命令加入指令队列最前面
            currentCmd.setHasRetryCount(hasRetryCount + 1);
            int curCmdNumber = IotChannelContextUtil.Cmd.getCurrentCmdNumber(ctx);

            if (currentCmd.getReadonly()) {
                IotChannelContextUtil.ReadCmd.addOneToFront(ctx, currentCmd);
            } else {
                IotChannelContextUtil.WriteCmd.addOneToFront(ctx, currentCmd);
            }
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.CMD_RETRY);
            log.info(IotChannelContextUtil.Log.context(ctx) + "[retry]将进行第{}条指令第{}次重试，指令信息：{}", curCmdNumber,
                    currentCmd.getHasRetryCount() - 1, currentCmd.toString());
        }
    }

    void executeNextCmd(ChannelHandlerContext ctx) {
        if (!checkAllowExecuteCmd(ctx)) {
            return;
        }
        NextCmdQuery nextCmdQuery = calculateNextCmdQuery(ctx);
        if (!nextCmdQuery.hasNextCmd) {
            if (log.isWarnEnabled()) {
                log.warn(IotChannelContextUtil.Log.context(ctx) + "[exec] 没有需要执行的指令！");
            }
            return;
        }
        doExecuteNextCmdReq(ctx, nextCmdQuery);
    }

    /**
     * 检查当前状态，是否可以执行指令
     *
     * @param ctx
     * @return
     */
    private boolean checkAllowExecuteCmd(ChannelHandlerContext ctx) {
        // 检查当前连接是否在线
        if (!ctx.channel().isActive()) {
            if (log.isWarnEnabled()) {
                log.warn(IotChannelContextUtil.Log.context(ctx) + "[exec] 执行指令失败，当前连接已断开！");
            }
            return false;
        }
        // 检查当前是否存在正在执行的指令
        if (IotChannelContextUtil.Cmd.hasExecutingCmd(ctx)) {
            if (log.isDebugEnabled()) {
                log.debug(IotChannelContextUtil.Log.context(ctx) + "[exec] 当前存在正在执行的指令，不用执行新指令。正在执行的指令: {}",
                        JsonUtils.writeValueAsString(IotChannelContextUtil.Cmd.getCurrentCmd(ctx)));
            }
            return false;
        }
        return true;
    }

    /**
     * 创建异步任务，执行下条指令。
     *
     * @param ctx
     * @param nextCmdQuery
     */
    private void doExecuteNextCmdReq(ChannelHandlerContext ctx, NextCmdQuery nextCmdQuery) {
        // 从指令队列中取写next指令
        AbstractIotCmdReq nextCmdReq = getNextCmdByCmdQuery(ctx, nextCmdQuery);
        if (nextCmdReq == null) {
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.IDLE_WITHOUT_COMMAND);
            log.error(IotChannelContextUtil.Log.context(ctx) + "[exec] 执行指令失败，没有可执行的指令！queueSize: {}, listSize: {}",
                    IotChannelContextUtil.ReadCmd.getQueueSize(ctx), IotChannelContextUtil.ReadCmd.getListSize(ctx));
            return;
        }
        // 更新指令执行状态
        IotChannelContextUtil.Cmd.setCurrentCmdWithStatus(ctx, nextCmdReq, IotCmdStatus.READY_FOR_SENDING);
        IotChannelContextUtil.Cmd.setCurrentCmdIsQueueLast(ctx, nextCmdQuery.readQueueLastCmd);
        // 计算执行下个指令，固定间隔时间
        doScheduleRunner(ctx, new IotCmdReqRunner(ctx, nextCmdReq, nextCmdQuery), MIN_INTERVAL_BETWEEN_COMMANDS_MS);
    }

    /**
     * 创建异步任务，执行下个指令队列。
     *
     * @param ctx
     */
    private void doSleep4NextQueue(ChannelHandlerContext ctx, ReadQueueResult queueResult) {
        doScheduleRunner(ctx, new IotSleep4NextQueueRunner(ctx, this), queueResult.nextDelayMs);
    }

    private void doScheduleRunner(ChannelHandlerContext ctx, Runnable runner, long nextDelayMs) {
        ScheduledFuture<?> oldFuture = IotChannelContextUtil.Cmd.getScheduledFuture(ctx);
        if (oldFuture != null) {
            oldFuture.cancel(true);
            if (log.isWarnEnabled()) {
                log.warn(IotChannelContextUtil.Log.context(ctx) + "[exec] 强制关闭未执行完毕的异步任务！future: {}",
                        oldFuture.hashCode());
            }
        }
        ScheduledFuture<?> newFuture = ctx.executor().schedule(runner, nextDelayMs, TimeUnit.MILLISECONDS);
        IotChannelContextUtil.Cmd.setScheduledFuture(ctx, newFuture);
    }

    AbstractIotCmdReq getNextCmdByCmdQuery(ChannelHandlerContext ctx, NextCmdQuery nextCmdQuery) {
        if (!nextCmdQuery.hasNextCmd) {
            return null;
        }
        if (nextCmdQuery.nextIsWriteCmd) {
            return IotChannelContextUtil.WriteCmd.pollQueue(ctx);
        }
        return IotChannelContextUtil.ReadCmd.pollQueue(ctx);
    }

    NextCmdQuery calculateNextCmdQuery(ChannelHandlerContext ctx) {
        NextCmdQuery query = new NextCmdQuery();
        int writeQueueSize = IotChannelContextUtil.WriteCmd.getQueueSize(ctx);
        if (writeQueueSize > 0) {
            query.hasNextCmd = true;
            query.nextIsWriteCmd = true;
            return query;
        }
        // 如果没有写指令，则从读指令队列中取读指令
        int listSize = IotChannelContextUtil.ReadCmd.getListSize(ctx);
        int queueSize = IotChannelContextUtil.ReadCmd.getQueueSize(ctx);
        if (listSize == 0) {
            // 读指令队列必为空
            query.readListEmpty = true;
            return query;
        }
        if (queueSize == 0) {
            // list不空，queue空了，那么需要重新生成队列
            int newQueueSize = IotChannelContextUtil.ReadCmd.rebuildQueue(ctx);
            query.readQueueFirstCmd = true;
            query.readQueueLastCmd = newQueueSize == 1;
            query.readQueueHasRebuilt = true;
        } else {
            // 读指令队列不空
            query.readQueueFirstCmd = queueSize == listSize;
            query.readQueueLastCmd = queueSize == 1;
        }
        query.hasNextCmd = true;
        return query;
    }

    private ReadQueueResult calculateReadQueueResult(ChannelHandlerContext ctx) {
        ReadQueueResult queueResult = new ReadQueueResult();
        // 上轮采集，首个读指令执行的开始时间
        Long beginTime = IotChannelContextUtil.Cmd.getQueueFirstCmdSendTimeMs(ctx);
        if (beginTime == null) {
            // 首次执行指令，还没有执行完读指令队列。
            queueResult.needSleep = false;
            queueResult.nextDelayMs = 0L;
            queueResult.isFinished = false;
            return queueResult;
        }
        // 执行完读指令队列耗时
        queueResult.costMs = System.currentTimeMillis() - beginTime;
        // 距离下轮采集开始时间的时间间隔
        long nextDelayMs = collectPollingIntervalMs - queueResult.costMs;
        if (nextDelayMs < 0) {
            queueResult.isTimeOver = true;
            nextDelayMs = MIN_INTERVAL_BETWEEN_COMMANDS_MS;
        }
        if (nextDelayMs > MIN_INTERVAL_BETWEEN_COMMANDS_MS) {
            queueResult.needSleep = true;
        }
        queueResult.nextDelayMs = nextDelayMs;
        return queueResult;
    }

    private static class ReadQueueResult {
        /**
         * 是否已完成读指令队列的采集
         */
        Boolean isFinished = true;
        /**
         * 是否需要延迟执行下个队列
         */
        Boolean needSleep = false;
        /**
         * 执行下轮采集前延迟时长
         */
        Long nextDelayMs = 0L;
        /**
         * 本轮执行耗时
         */
        Long costMs = 0L;
        /**
         * 本轮执行耗时是否超过了采集周期
         */
        Boolean isTimeOver = false;
    }

    public static class NextCmdQuery {
        /**
         * 是否存在下一条指令
         */
        boolean hasNextCmd = false;
        /**
         * 前提条件：hasNextCmd = true<br/>
         * 是否为写指令
         */
        boolean nextIsWriteCmd = false;
        /**
         * 前提条件：<br/>
         * hasNextCmd = true<br/>
         * nextIsWriteCmd = false<br/>
         * 将要执行的指令是否为读队列的第一条指令。
         */
        boolean readQueueFirstCmd = false;
        /**
         * 前提条件：<br/>
         * hasNextCmd = true<br/>
         * nextIsWriteCmd = false<br/>
         * 将要执行的指令是否为读队列的最后一条指令。
         */
        boolean readQueueLastCmd = false;
        /**
         * 前提条件：<br/>
         * hasNextCmd = true<br/>
         * nextIsWriteCmd = false<br/>
         * 读指令的静态列表为空。
         */
        boolean readListEmpty = false;

        /**
         * 只读队列执行完毕，重新生成了下轮采集的读指令队列
         */
        boolean readQueueHasRebuilt = false;
    }

    static class IotSleep4NextQueueRunner implements Runnable {
        private final ChannelHandlerContext ctx;
        private final CmdExecuteServiceImpl execService;

        public IotSleep4NextQueueRunner(ChannelHandlerContext ctx, CmdExecuteServiceImpl execService) {
            this.ctx = ctx;
            this.execService = execService;
        }

        @Override
        public void run() {
            // 清除异步任务句柄。
            IotChannelContextUtil.Cmd.clearScheduledFuture(ctx);
            this.execService.executeNextCmd(ctx);
        }
    }

    static class IotCmdReqRunner implements Runnable {
        private final ChannelHandlerContext ctx;
        private final AbstractIotCmdReq cmdReq;
        private final NextCmdQuery cmdQuery;

        public IotCmdReqRunner(ChannelHandlerContext ctx, AbstractIotCmdReq cmdReq, NextCmdQuery cmdQuery) {
            this.ctx = ctx;
            this.cmdReq = cmdReq;
            this.cmdQuery = cmdQuery;
        }

        @Override
        public void run() {
            // 清除异步任务句柄。
            IotChannelContextUtil.Cmd.clearScheduledFuture(ctx);
            if (!ctx.channel().isActive()) {
                IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.END_INACTIVE);
                return;
            }
            if (cmdQuery.nextIsWriteCmd) {
                if (log.isDebugEnabled()) {
                    log.debug(IotChannelContextUtil.Log.context(ctx) + "[exec] 将执行写指令, 本轮剩余读指令共{}条",
                            IotChannelContextUtil.ReadCmd.getQueueSize(ctx));
                }
            } else {
                if (cmdQuery.readQueueFirstCmd) {
                    IotChannelContextUtil.Cmd.setQueueFirstCmdSendTimeMs(ctx, System.currentTimeMillis());
                }

                int listSize = IotChannelContextUtil.ReadCmd.getListSize(ctx);
                int curCmdNumber = listSize - IotChannelContextUtil.ReadCmd.getQueueSize(ctx);
                IotChannelContextUtil.Cmd.setCurrentCmdNumber(ctx, curCmdNumber);

                if (log.isDebugEnabled()) {
                    log.debug(IotChannelContextUtil.Log.context(ctx) + "[exec] 将执行第{}条读指令，读指令队列共{}条。", curCmdNumber,
                            listSize);
                }
            }
            ctx.writeAndFlush(cmdReq);
            IotChannelContextUtil.Cmd.setCmdStatus(ctx, IotCmdStatus.SENT_AND_WAITING_RESPONSE);
        }
    }
}
