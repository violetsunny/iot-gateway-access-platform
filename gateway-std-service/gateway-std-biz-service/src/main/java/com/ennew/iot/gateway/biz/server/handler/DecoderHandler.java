package com.ennew.iot.gateway.biz.server.handler;

import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 解码器
 *
 * @author hanyilong@enn.cn
 */
@Slf4j
@Data
public class DecoderHandler extends ByteToMessageDecoder {
    ProtocolSupport protocol;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记一些当前的readIndex
        in.markReaderIndex();
        int length = in.readableBytes();

        byte[] messageBytes;
        if (in.hasArray()) {
            ByteBuf slice = in.slice();
            messageBytes = slice.array();
        } else {
            messageBytes = new byte[length];
            in.readBytes(messageBytes, 0, length);
        }
        log.info("收到消息：{}", new String(messageBytes));
        Message outMsg = protocol.getMessageCodec(DefaultTransport.TCP).parseFrom(messageBytes);
        if (outMsg != null) {
            out.add(outMsg);
        }
    }

}
