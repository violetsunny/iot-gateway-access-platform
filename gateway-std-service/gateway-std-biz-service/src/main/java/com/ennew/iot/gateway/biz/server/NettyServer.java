//package com.ennew.iot.gateway.biz.server;
//
//import com.ennew.iot.gateway.biz.config.ServerConfig;
//import com.ennew.iot.gateway.biz.config.ServerConfigs;
//import com.ennew.iot.gateway.biz.jarload.JarProtocolSupportLoader;
//import com.ennew.iot.gateway.client.protocol.Protocol;
//import com.ennew.iot.gateway.biz.server.handler.*;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.buffer.UnpooledByteBufAllocator;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.timeout.IdleStateHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * Netty 网关
// *
// * @author hanyilong@enn.cn
// */
//@Slf4j
//@Service("nettyServer")
//public class NettyServer {
//
//    private ServerConfigs configs;
//    private EventLoopGroup boss = new NioEventLoopGroup();
//    private EventLoopGroup work = new NioEventLoopGroup();
//
//    @Autowired
//    private LoginHandler loginHandler;
//
//    @Autowired
//    private DataReportHandler dataReportHandler;
//
//    @Autowired
//    private ExceptionHandler exceptionHandler;
//
//    ServerBootstrap serverBootstrap = new ServerBootstrap();
//    JarProtocolSupportLoader loader = new JarProtocolSupportLoader();
//
//
//    public void setConfigs(ServerConfigs configs) {
//        this.configs = configs;
//    }
//    public ServerConfigs getConfigs() {
//        return configs;
//    }
//
//
//    /**
//     * 启动Netty服务
//     */
//    public void start() {
//
//        serverBootstrap
//                .group(boss, work)
//                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_REUSEADDR, true)
//                // ByteBuf 分配器
//                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
//                .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
//                .childHandler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        int port = ch.localAddress().getPort();
//                        ServerConfig config = findServerConfigByPort(port);
//                        // Protocol protocol = (Protocol) Class.forName(config.getProtocol()).newInstance();
//                        Protocol protocol = loader.load(config.getProtocolPath(), config.getProtocol());
//
//                        // 心跳检测，超时时间120s
//                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
//                        // 自定义编解码器
//                        DecoderHandler decoderHandler = new DecoderHandler();
//                        decoderHandler.setProtocol(protocol);
//                        ch.pipeline().addLast("decoder", decoderHandler);
//                        EncodeHandler encodeHandler = new EncodeHandler();
//                        encodeHandler.setProtocol(protocol);
//                        ch.pipeline().addLast("encoder", encodeHandler);
//                        // 业务处理
//                        loginHandler.setProtocol(protocol);
//                        ch.pipeline().addLast("login", loginHandler);
//                        dataReportHandler.setProtocol(protocol);
//                        ch.pipeline().addLast("dataReport", dataReportHandler);
//                        ch.pipeline().addLast("exception", exceptionHandler);
//                    }
//                });
//
//        List<ChannelFuture> channelFutures = new ArrayList<>();
//
//        boolean isStart = false;
//
//        for(ServerConfig config : configs.getServerConfigs()){
//            try{
//                ChannelFuture channelFuture = serverBootstrap.bind(config.getIp(), config.getPort()).sync();
//                channelFutures.add(channelFuture);
//                log.info("Netty 服务启动成功，端口：{}", channelFuture.channel().localAddress());
//            }catch (Exception e){
//                log.error("启动 Netty 服务时发生异常", e);
//            }
//        }
//
//    }
//
//    private ServerConfig findServerConfigByPort(int port) {
//        for(ServerConfig config : configs.getServerConfigs()){
//            if(config.getPort() == port){
//                return config;
//            }
//        }
//        return null;
//    }
//
//}
