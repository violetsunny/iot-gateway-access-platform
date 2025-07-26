package com.ennew.iot.gateway.biz.server.handler;

import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Data;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-09 10:57:16
 */
@Data
public class EncodeHandler extends MessageToByteEncoder {

    private ProtocolSupport protocol;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 发送字节码消息
        if (msg instanceof Message) {
            Message message = (Message) msg;
            out.writeBytes(protocol.getMessageCodec(DefaultTransport.TCP).toByteArray(message));
        }
    }


}
