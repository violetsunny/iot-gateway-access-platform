package com.enn.iot.dtu.handler.channel;

import com.enn.iot.dtu.common.properties.IotProperties;
import com.enn.iot.dtu.handler.*;
import com.enn.iot.dtu.integration.kafka.IotKafkaClient;
import com.enn.iot.dtu.service.CmdExecuteService;
import com.enn.iot.dtu.service.MainDataService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@Component
public class IotChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final IotProperties iotProperties;
    private final IotKafkaClient iotKafkaClient;
    private final MainDataService mainDataService;
    private final CmdExecuteService cmdExecService;
    private LoggingHandler iot01LoggingHandler;
    private GlobalTrafficShapingHandler iot02GlobalTrafficShapingHandler;
    private Iot05DtuAuthHandler iot05DtuAuthHandler;
    private Iot06DtuHeartBeatHandler iot06DtuHeartBeatHandler;
    private Iot07ProtocolDecoder iot07ProtocolDecoder;
    private Iot08ProtocolEncoder iot08ProtocolEncoder;
    private Iot10TaskHandler iot10TaskHandler;
    private Iot11DataSender iot11DataSender;
    private Iot12EventHandler iot12EventHandler;
    private Iot13EventSender iot13EventSender;

    public IotChannelInitializer(IotProperties iotProperties, MainDataService mainDataService,
                                 CmdExecuteService cmdExecService, IotKafkaClient iotKafkaClient) {
        this.iotProperties = iotProperties;
        this.mainDataService = mainDataService;
        this.cmdExecService = cmdExecService;
        this.iotKafkaClient = iotKafkaClient;
    }

    @PostConstruct
    public void initSharableHandler() {
        this.iot01LoggingHandler = new LoggingHandler(LogLevel.DEBUG);
        this.iot02GlobalTrafficShapingHandler = newIot02GlobalTrafficShapingHandler();
        this.iot05DtuAuthHandler = new Iot05DtuAuthHandler(mainDataService);
        this.iot06DtuHeartBeatHandler = new Iot06DtuHeartBeatHandler();
        this.iot07ProtocolDecoder = new Iot07ProtocolDecoder();
        this.iot08ProtocolEncoder = new Iot08ProtocolEncoder();
        this.iot10TaskHandler = new Iot10TaskHandler(cmdExecService);
        this.iot11DataSender = new Iot11DataSender(iotKafkaClient);
        this.iot12EventHandler = new Iot12EventHandler(mainDataService, cmdExecService, iotProperties);
        this.iot13EventSender = new Iot13EventSender(iotKafkaClient);
    }

    private Iot02GlobalTrafficShapingHandler newIot02GlobalTrafficShapingHandler() {
        int corePoolSize = 1;
        ScheduledThreadPoolExecutor executor =
                new ScheduledThreadPoolExecutor(corePoolSize, new DefaultThreadFactory("iot-traffic"));
        return new Iot02GlobalTrafficShapingHandler(executor, iotProperties.getTrafficCheckInterval().toMillis());
    }

    /**
     * 当新连接accept的时候，这个方法会调用
     *
     * @param socketChannel
     *            the Channel which was registered.
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 为当前的channel的pipeline添加自定义的处理函数
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        if (log.isDebugEnabled()) {
            channelPipeline.addLast(this.iot01LoggingHandler);
        }
        channelPipeline.addLast(this.iot02GlobalTrafficShapingHandler);
        channelPipeline.addLast(new Iot03IdleStateHandler(iotProperties));
        channelPipeline.addLast(new Iot04ProtocolFrameDecoder());
        channelPipeline.addLast(this.iot05DtuAuthHandler);
        channelPipeline.addLast(this.iot06DtuHeartBeatHandler);
        channelPipeline.addLast(this.iot07ProtocolDecoder);
        channelPipeline.addLast(this.iot08ProtocolEncoder);
        channelPipeline.addLast(new Iot09ResponseTimeoutHandler(iotProperties));
        channelPipeline.addLast(this.iot10TaskHandler);
        channelPipeline.addLast(this.iot11DataSender);
        channelPipeline.addLast(this.iot12EventHandler);
        channelPipeline.addLast(this.iot13EventSender);
    }
}
