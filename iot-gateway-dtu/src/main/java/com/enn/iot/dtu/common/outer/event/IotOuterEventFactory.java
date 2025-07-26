package com.enn.iot.dtu.common.outer.event;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Auth;
import io.netty.channel.ChannelHandlerContext;

public class IotOuterEventFactory {

    public static IotOuterEvent createConnectedEvent(ChannelHandlerContext ctx, Long clientConnectedAt, Long eventTime){
        IotOuterEvent event = new IotOuterEvent();
        event.tags.setEvent(IotOuterEventEnum.CLIENT_CONNECTED);
        event.tags.setGatewaySn(Auth.getGatewaySn(ctx));
        event.tags.setChannelId(ctx.channel().id().asLongText());
        event.fields.setConnectedAt(clientConnectedAt);
        event.setTime(eventTime);
        return event;
    }
}
