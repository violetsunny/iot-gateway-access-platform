package com.enn.iot.dtu.handler;

import cn.hutool.json.JSONObject;
import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.outer.msg.dto.KafkaPropertyMessage;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.kafka.IotKafkaClient;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class Iot11DataSender extends SimpleChannelInboundHandler<List<KafkaPropertyMessage>> {

    private final IotKafkaClient iotKafkaClient;

    public Iot11DataSender(IotKafkaClient iotKafkaClient) {
        this.iotKafkaClient = iotKafkaClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, List<KafkaPropertyMessage> msgs) {
        msgs.forEach(msg -> {
            if (log.isTraceEnabled()) {
                log.trace(IotChannelContextUtil.Log.context(ctx) + "[11] 待发送数据:\n{}", JsonUtils.writeValueAsString(msg));
            }
            try {
                log.debug(IotChannelContextUtil.Log.context(ctx) + "modbus-RTU数据接收:{}", JsonUtils.writeValueAsString(msg));
                iotKafkaClient.sendUncimData(msg);
            } catch (Exception e) {
                log.error(IotChannelContextUtil.Log.context(ctx) + "[11] kafka发送uncim数据失败！数据:\n"
                        + JsonUtils.writeValueAsString(msg), e);
            }
        });
    }
}
