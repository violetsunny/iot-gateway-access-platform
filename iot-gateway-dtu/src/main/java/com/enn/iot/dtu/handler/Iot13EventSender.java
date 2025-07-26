package com.enn.iot.dtu.handler;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.outer.event.IotOuterEvent;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.kafka.IotKafkaClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 对外事件发送处理器
 **/
@Slf4j
@ChannelHandler.Sharable
public class Iot13EventSender extends ChannelInboundHandlerAdapter {

    private final IotKafkaClient iotKafkaClient;

    public Iot13EventSender(IotKafkaClient iotKafkaClient) {
        this.iotKafkaClient = iotKafkaClient;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (log.isTraceEnabled()) {
            log.trace(IotChannelContextUtil.Log.context(ctx) + "[12] 待发送数据:\n{}", JsonUtils.writeValueAsString(evt));
        }
        if (evt instanceof IotOuterEvent) {
            try {
//                iotKafkaClient.sendEventData((IotOuterEvent)evt);
            } catch (Exception e) {
                log.error(IotChannelContextUtil.Log.context(ctx) + "[12] kafka 发送 event 数据失败！数据:\n"
                        + JsonUtils.writeValueAsString(evt), e);
            }
        }
    }


}
