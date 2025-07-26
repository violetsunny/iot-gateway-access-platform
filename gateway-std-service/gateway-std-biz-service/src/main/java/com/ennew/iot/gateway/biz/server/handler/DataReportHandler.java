package com.ennew.iot.gateway.biz.server.handler;

import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.biz.queue.CacheQueue;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.client.protocol.model.ReportResponse;
import com.ennew.iot.gateway.common.utils.CommonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hanyilong@enn.cn
 */
@Slf4j
@Service
@Data
@ChannelHandler.Sharable
public class DataReportHandler extends ChannelInboundHandlerAdapter {


    @Autowired
    private UpDataTransfer upDataTransfer;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        Message message = (Message) msg;
        MessageType type = message.getMessageType();
        if (type.equals(MessageType.REPORT_REQ)) {
            // 数据上报消息
            ReportRequest reportRequest = (ReportRequest)message;
            upDataTransfer.handlerUpData(message);
            log.debug("收到数据上报消息：{}", reportRequest);

            ReportResponse reportResponse = new ReportResponse();
            reportResponse.setResult(true);
            reportResponse.setTimeStamp(System.currentTimeMillis());
            reportResponse.setMessageId(CommonUtils.getUUID());

            ctx.writeAndFlush(reportResponse);
        }
    }


}
