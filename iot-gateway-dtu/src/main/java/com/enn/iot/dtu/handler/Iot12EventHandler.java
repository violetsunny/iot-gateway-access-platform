package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.event.AbstractIotEvent;
import com.enn.iot.dtu.common.event.IotAuthEvent;
import com.enn.iot.dtu.common.outer.event.IotOuterEventFactory;
import com.enn.iot.dtu.common.properties.IotProperties;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.service.CmdExecuteService;
import com.enn.iot.dtu.service.MainDataService;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Auth;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil.Channels;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Connection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
@AllArgsConstructor
public class Iot12EventHandler extends ChannelInboundHandlerAdapter {

    private final MainDataService mainDataService;
    private final CmdExecuteService cmdExecService;
    private final IotProperties iotProperties;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.error(Log.context(ctx) + "[12]事件: 未处理消息，消息: {}", msg.toString());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof AbstractIotEvent) {
            // 认证事件
            if (evt instanceof IotAuthEvent) {
                doAuthEvent(ctx, (IotAuthEvent)evt);
            }
            // 未处理事件
            else {
                log.warn(Log.context(ctx) + "[12]事件: 未处理事件，事件: {}", JsonUtils.writeValueAsString(evt));
            }
        } else if (evt instanceof IdleStateEvent) {
            doIdleTimeoutEvent(ctx, (IdleStateEvent)evt);
        } else {
            // 未识别事件
            log.warn(Log.context(ctx) + "[12]事件: 未识别事件，事件: {},{}", evt.getClass().toString(),
                    JsonUtils.writeValueAsString(evt));
        }
    }

    /**
     * 认证事件：认证成功、认证失败
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param event
     *            事件对象
     */
    private void doAuthEvent(ChannelHandlerContext ctx, IotAuthEvent event) {
        String gatewaySn = event.getGatewaySn();
        if (event.isSuccess()) {
            // 1、更新认证状态
            Auth.setAuthed(ctx, gatewaySn);
            if (log.isInfoEnabled()) {
                log.info(Log.context(ctx) + "[12]事件: 认证成功事件");
            }
            // 2、检查是否存在一个网关标识重复登录的现象
            checkAndHandleDuplicateLogin(ctx, gatewaySn);
            // 3、缓存Channel引用
            Channels.addChannel(gatewaySn, ctx.channel());
            // 4、 认证成功之后的首次加载DTU档案信息
            try {
                mainDataService.refreshMainDataIf(ctx);
            } catch (Exception e) {
                log.error(Log.context(ctx) + "[12]刷新配置异常！", e);
            }
            ctx.fireUserEventTriggered(
                    IotOuterEventFactory.createConnectedEvent(ctx, Connection.getConnectedTimeMs(ctx), event.getTimeMs()));
        } else {
            log.warn(Log.context(ctx) + "[12]事件:认证失败事件,断开连接！ 失败原因: {}", event.getMessage());
            // 认证失败断开连接
            ctx.close();
        }
    }

    /**
     * 检测是否多处登录；如果存在多个连接，则关闭老连接。<br/>
     * TODO 目前只实现了，应用节点级别的重复登录检查，后续可进一步实现全局的重复登录的检查
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param gatewaySn
     *            网关标识
     */
    private void checkAndHandleDuplicateLogin(ChannelHandlerContext ctx, String gatewaySn) {
        Channel oldChannel = Channels.getChannel(gatewaySn);
        boolean isDuplicateLogin = null != oldChannel;
        if (isDuplicateLogin) {
            // 如果存在一个网关标识多次登录，那么关闭老连接
            oldChannel.close();
            IotGlobalContextUtil.deleteContext(gatewaySn);
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[12]事件: 存在重复登录的连接，关闭之前的连接！老连接: [0x{}]", oldChannel.id().asShortText());
            }
        }
    }

    /**
     * 通讯超时事件
     *
     * @param ctx
     *            the ChannelHandlerContext
     * @param event
     *            事件对象
     */
    private void doIdleTimeoutEvent(ChannelHandlerContext ctx, IdleStateEvent event) {
        boolean isAuthTimeout = false;
        if (event == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            boolean hasAuthed = Auth.hasAuthed(ctx);
            if (!hasAuthed) {
                isAuthTimeout = true;
                if (log.isWarnEnabled()) {
                    log.warn(Log.context(ctx) + "[12]事件: 等待认证报文超时，断开连接！ 读空闲时间: {}, state: {}, first: {}",
                            iotProperties.getReaderIdleTimeout().toString(), event.state(), event.isFirst());
                }
            }
        }
        if (!isAuthTimeout) {
            if (log.isWarnEnabled()) {
                switch (event.state()) {
                    case READER_IDLE:
                        log.warn(Log.context(ctx) + "[12]事件: 通讯超时，读空闲，断开连接！读空闲时间: {}, first: {}",
                                iotProperties.getReaderIdleTimeout().toString(), event.isFirst());
                        break;
                    case WRITER_IDLE:
                        log.warn(Log.context(ctx) + "[12]事件: 通讯超时，写空闲，断开连接！写空闲时间: {}, first: {}",
                                iotProperties.getWriterIdleTimeout().toString(), event.isFirst());
                        break;
                    // case ALL_IDLE:
                    default:
                        log.warn(Log.context(ctx) + "[12]事件: 通讯超时，读写空闲，断开连接！读写空闲时间: {}, first: {}",
                                iotProperties.getAllIdleTimeout().toString(), event.isFirst());
                        break;
                }
            }
        }
        ctx.close();
    }
}
