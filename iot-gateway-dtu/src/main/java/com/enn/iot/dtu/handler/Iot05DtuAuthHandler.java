package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.event.IotAuthEvent;
import com.enn.iot.dtu.service.MainDataService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@ChannelHandler.Sharable
public class Iot05DtuAuthHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final MainDataService mainDataService;

    public Iot05DtuAuthHandler(MainDataService mainDataService) {
        this.mainDataService = mainDataService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frameBuf) {
        // 当前连接是否已认证
        if (IotChannelContextUtil.Auth.hasAuthed(ctx)) {
            // 是否已认证过，向后传播
            ctx.fireChannelRead(frameBuf.retainedDuplicate());
            return;
        }
        // 若未认证，检查认证报文（长度、内容）
        String rawData = ByteBufUtil.hexDump(frameBuf);
        String registerPackage = frameBuf.toString(CharsetUtil.US_ASCII);
        String errorMsg = validateByLocal(registerPackage);
        if (errorMsg != null) {
            // 认证失败，发送"认证失败"Event事件
            ctx.fireUserEventTriggered(IotAuthEvent.failed(errorMsg + ", 认证报文: 0x" + rawData + "，" + registerPackage));
            return;
        }
        registerPackage = registerPackage.trim();
//        String gatewaySn = validateByRemote(ctx, registerPackage);
        String gatewaySn = registerPackage;
        if (gatewaySn == null) {
            // 认证失败，发送"认证失败"Event事件
            ctx.fireUserEventTriggered(IotAuthEvent.failed("接口认证失败, 认证报文: 0x" + rawData + "，" + registerPackage));
            return;
        }
        // 认证成功，发送"认证成功"Event事件
        ctx.fireUserEventTriggered(IotAuthEvent.success("认证成功", gatewaySn));
        // the MessageEvent end
    }

    /**
     * 认证报文本地合规性校验<br/>
     * only contains letters or digits or _, and is non-null
     *
     * @param gatewaySn 网关标识
     * @return error message
     */
    String validateByLocal(String gatewaySn) {
        if (null == gatewaySn) {
            return "认证失败，认证报文不能为空！";
        }
        final int minLength = 6;
        final int maxLength = 23;
        if (gatewaySn.length() < minLength || gatewaySn.length() > maxLength) {
            return "认证失败，认证报文长度不能小于" + minLength + "，大于" + maxLength + "！";
        }
        gatewaySn = gatewaySn.trim();
        gatewaySn = gatewaySn.replace("_", "");
        if (!StringUtils.isAlphanumeric(gatewaySn)) {
            return "认证失败，认证报文只能包含字母、数字、下划线！";
        }
        return null;
    }

    /**
     * 认证报文远端检查是否已注册
     *
     * @param registerPackage 注册包
     * @return error message
     */
    String validateByRemote(ChannelHandlerContext ctx, String registerPackage) {
        // 检查网关标识是否已注册
        try {
            return mainDataService.getGatewaySnByRegisterPackage(registerPackage);
        } catch (Exception e) {
            log.error(IotChannelContextUtil.Log.context(ctx) + "[05] 认证接口异常！", e);
            return null;
        }
    }
}
