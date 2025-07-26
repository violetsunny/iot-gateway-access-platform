package com.ennew.iot.gateway.biz.gateway.tcp;

import com.ennew.iot.gateway.biz.gateway.AbstractDeviceGateway;
import com.ennew.iot.gateway.biz.gateway.supports.DeviceGatewayProperties;
import com.ennew.iot.gateway.biz.gateway.supports.NetworkConfigProperties;
import com.ennew.iot.gateway.biz.server.handler.*;
import com.ennew.iot.gateway.biz.session.SessionManger;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.Transport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpServerDeviceGateway extends AbstractDeviceGateway {

    private final String host;
    private final Integer port;
    private final Integer bossGroupThreadCount;
    private final Integer workerGroupThreadCount;
    private final boolean keepAlive;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    //    private final TcpTransportContext context;
    private final ProtocolSupport protocolSupport;
    private final LoginHandler loginHandler;
    private final DataReportHandler dataReportHandler;
    private final ExceptionHandler exceptionHandler;

    public TcpServerDeviceGateway(DeviceGatewayProperties properties, ProtocolSupport protocolSupport, SessionManger sessionManger, LoginHandler loginHandler, DataReportHandler dataReportHandler, ExceptionHandler exceptionHandler) {
        super(properties.getId());

        NetworkConfigProperties networkConfigProperties = properties.getNetworkConfigProperties();
        Map<String, Object> networkConfiguration = networkConfigProperties.getConfiguration();

//        host = (String) networkConfiguration.getOrDefault("host", "127.0.0.1");
        host = "0.0.0.0";
        Object portObj = networkConfiguration.get("port");
        Assert.notNull(portObj, "port不能为空");
        port = Integer.parseInt(String.valueOf(portObj));

        String leakDetectorLevel = (String) networkConfiguration.getOrDefault("leakDetectorLevel", "simple");
        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel.toUpperCase()));

        bossGroupThreadCount = (Integer) networkConfiguration.getOrDefault("bossGroupThreadCount", 1);
        workerGroupThreadCount = (Integer) networkConfiguration.getOrDefault("workerGroupThreadCount", Runtime.getRuntime().availableProcessors());
        keepAlive = (boolean)networkConfiguration.getOrDefault("keepAlive", true);

//        context = TcpTransportContext.of(getId(), getTransport(), protocolSupport.getMessageCodec(getTransport()), sessionManger);
        this.protocolSupport = protocolSupport;
        this.loginHandler = loginHandler;
        this.dataReportHandler = dataReportHandler;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void doStartup() {
        if (serverChannel != null) {
            shutdown();
        }

        log.info("Starting TCP gateway...");
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        if (!isStarted()) {
                            log.error("tcp网关未启动");
                            ch.close();
                            return;
                        }
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast("decoder", new DirectRecordDecoder());
//                        TcpTransportHandler handler = new TcpTransportHandler(context);
//                        pipeline.addLast(handler);
//                        ch.closeFuture().addListener(handler);

                        // 心跳检测，超时时间120s
                        pipeline.addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
                        // 自定义编解码器
                        DecoderHandler decoderHandler = new DecoderHandler();
                        decoderHandler.setProtocol(protocolSupport);
                        pipeline.addLast("decoder", decoderHandler);
                        EncodeHandler encodeHandler = new EncodeHandler();
                        encodeHandler.setProtocol(protocolSupport);
                        pipeline.addLast("encoder", encodeHandler);
                        // 业务处理
                        loginHandler.setProtocol(protocolSupport);
                        pipeline.addLast("login", loginHandler);
                        pipeline.addLast("dataReport", dataReportHandler);
                        pipeline.addLast("exception", exceptionHandler);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, keepAlive);
        try {
            serverChannel = serverBootstrap.bind(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("TCP transport started!");
    }

    @Override
    public void doShutdown() {
        log.info("Stopping TCP transport!");
        try {

            serverChannel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("TCP transport stopped!");
    }

    @Override
    public Transport getTransport() {
        return DefaultTransport.TCP;
    }
}
