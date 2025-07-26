package com.enn.iot.dtu.server;

import com.enn.iot.dtu.common.constant.IotCommonsConstant;
import com.enn.iot.dtu.handler.channel.IotChannelInitializer;
import com.enn.iot.dtu.properties.IotNettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IotServer {
    private static final String BOSS_GROUP_THREAD_PREFIX = "iot-boss";
    private static final String WORKER_GROUP_THREAD_PREFIX = "iot-worker";
    /**
     * 初始化Channel中的handler
     */
    private final IotChannelInitializer channelInitializer;
    private final IotNettyProperties iotNettyProperties;
    /**
     * boss线程池（用于接收client请求）
     */
    private final NioEventLoopGroup bossGroup;
    /**
     * worker线程池（用于处理具体的读写操作）
     */
    private final NioEventLoopGroup workerGroup;
    @Value("${env:DEV}")
    private String env;

    public IotServer(IotNettyProperties iotNettyProperties, IotChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
        this.iotNettyProperties = iotNettyProperties;
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(BOSS_GROUP_THREAD_PREFIX));
        workerGroup = new NioEventLoopGroup(iotNettyProperties.getWorkerEventLoopThreads(),
                new DefaultThreadFactory(WORKER_GROUP_THREAD_PREFIX));
    }

    private void setServerOption(ServerBootstrap bootstrap) {
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    private void setChildOption(ServerBootstrap bootstrap) {
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    private void initResourceLeakDetector() {
        // DISABLED ：完全禁用内存泄露检测，不推荐
        // SIMPLE ：抽样1%的ByteBuf，提示是否有内存泄露
        // ADVANCED ：抽样1%的ByteBuf，提示哪里产生了内存泄露
        // PARANOID ：对每一个ByteBu进行检测，提示哪里产生了内存泄露
        if (IotCommonsConstant.PROPERTIES_KEY_ENV_VALUE_DEV.equals(this.env)) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        }
    }

    public void startup() {
        initResourceLeakDetector();
        // 启动 NIO 服务的辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                // 用它来建立新accept的连接，用于构造ServerSocketChannel的工厂类
                .channel(NioServerSocketChannel.class)
                // 为accept channel的pipeline预添加的InboundHandler
                .childHandler(this.channelInitializer);
        setServerOption(bootstrap);
        setChildOption(bootstrap);
        try {
            // 绑定端口，开始接收进来的连接
            ChannelFuture startFuture = bootstrap.bind(iotNettyProperties.getPort()).sync();
            if (startFuture.isSuccess()) {
                log.info("服务器启动成功，监听端口({})，工作线程数: {}", iotNettyProperties.getPort(), workerGroup.executorCount());
            } else {
                log.error("服务启动失败，监听端口(" + iotNettyProperties.getPort() + ")", startFuture.cause());
            }
        } catch (InterruptedException e) {
            log.error("服务器Socket中断异常！", e);
        }
    }

    public void shutdownGracefully() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
