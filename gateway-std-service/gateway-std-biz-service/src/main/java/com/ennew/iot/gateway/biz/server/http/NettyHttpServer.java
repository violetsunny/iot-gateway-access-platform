package com.ennew.iot.gateway.biz.server.http;

import cn.hutool.core.lang.Assert;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLEngine;
import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;



/**
 * Netty HTTP Server 组件
 */
public class NettyHttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServer.class);

    public static final String METHOD_URI_ROUTE_FORMAT = "{0}_{1}";
    public static final String COMMON_ROUTE = "*";


    /**
     * Netty HTTP Server 配置
     */
    private final NettyHttpServerOptions options;

    /**
     * 路由 Mapper
     */
    private final Map<String, NettyHttpRequestHandler> routingMap;

    /**
     * bossGroup
     */
    private EventLoopGroup bossGroup;

    /**
     * workerGroup
     */
    private EventLoopGroup workerGroup;

    /**
     * ssl context
     */
    private SslContext sslContext;

    /**
     * 业务现称池，异步处理业务 handler
     */
    private EventExecutorGroup asyncHandlerExecutor;

    /**
     * Netty Channel
     */
    private Channel channel;


    public NettyHttpServer(NettyHttpServerOptions options) {
        Assert.notNull(options, "NettyHttpServer options is null");
        Assert.notNull(options.getPort(), "NettyHttpServer options.port not configure");
        this.options = options;
        routingMap = new HashMap<>();
        LOGGER.debug("init NettyHttpServer, {}", options);
    }


    /**
     * 添加请求处理器
     *
     * @param handler Http请求处理器
     */
    public void addHandler(NettyHttpRequestHandler handler) {
        routingMap.put(COMMON_ROUTE, handler);
    }


    /**
     * 添加请求处理器
     *
     * @param method HTTP请求方法
     * @param path HTTP请求路径
     * @param handler Http请求处理器
     */
    public void addHandler(HttpMethod method, String path, NettyHttpRequestHandler handler){
        routingMap.put(MessageFormat.format(METHOD_URI_ROUTE_FORMAT, method.name(), path), handler);
    }


    /**
     * 添加请求处理器
     *
     * @param path HTTP请求路径
     * @param handler Http请求处理器
     */
    public void addHandler(String path, NettyHttpRequestHandler handler){
        routingMap.put(path, handler);
    }


    /**
     * 启动HTTP Server
     */
    public void startup() {
        if (options.isSslEnabled()) {
            initSSlContext();
        }
        if (options.isAsyncHandlerEnabled()) {
            initAsyncHandlerExecutor();
        }
        bossGroup = new NioEventLoopGroup(options.getBossGroupThreadCount());
        workerGroup = new NioEventLoopGroup(options.getWorkerGroupThreadCount());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, options.getSoBacklog());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                addSSLHandler(options.isSslEnabled(), socketChannel);
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(options.getMaxContentLength()));
                pipeline.addLast(asyncHandlerExecutor, new NettyHttpServerHandler(routingMap));
            }
        });
        String host = options.getHost();
        int port = options.getPort();
        ChannelFuture future = bootstrap.bind(host, port);
        channel = future.channel();
        future.addListener(cf -> {
            if (cf.isSuccess()) {
                LOGGER.info("Http server running at http{}://{}:{}", options.isSslEnabled()? "s":"", host, port);
            } else {
                LOGGER.error("Http server startup failed", cf.cause());
            }
        });
    }


    /**
     * 停止 HTTP Server
     */
    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (asyncHandlerExecutor != null) {
            asyncHandlerExecutor.shutdownGracefully();
        }
    }

    /**
     * HTTP Server 是否运行
     *
     * @return true是，false否
     */
    public boolean isRunning(){
        return channel.isActive();
    }


    /**
     * 添加 SSL handler
     *
     * @param sslEnabled SSL是否启用
     * @param socketChannel Netty SocketChannel
     */
    private void addSSLHandler(boolean sslEnabled, SocketChannel socketChannel) {
        if(sslEnabled){
            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
            sslEngine.setUseClientMode(false);
            socketChannel.pipeline().addLast(new SslHandler(sslEngine));
        }
    }


    /**
     * 初始化异步handler线程池
     */
    private void initAsyncHandlerExecutor() {
        ThreadFactory bizThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("NettyHttpServerExecutor-%d")
                .build();
        asyncHandlerExecutor = new DefaultEventExecutorGroup(options.getAsyncHandlerThreadCount(), bizThreadFactory);
    }


    /**
     * 初始化 SSL Context
     */
    private void initSSlContext() {
        try {
            String certPath = options.getSslCertPath();
            String keyPath = options.getSslKeyPath();
            String crtPath = options.getSslCrtPath();
            LOGGER.info("SSL model enabled, cert: {}, key: {}, crt: {}", certPath, keyPath, crtPath);
            File cert, key, crt;
            cert = new File(certPath);
            key = new File(keyPath);
            crt = new File(crtPath);
            // 目录中不存在从jar中加载
            if (!cert.exists() && !key.exists() && !crt.exists()) {
                String certFileName = FilenameUtils.getName(certPath);
                String keyFileName = FilenameUtils.getName(keyPath);
                String crtFileName = FilenameUtils.getName(crtPath);
                LOGGER.debug("SSL certificate file not exists in file system, will load from jar, {}, {}, {}",
                        certFileName, keyFileName, crtFileName);
                cert = new File(options.getTmpFileDirectory().concat(certFileName));
                key = new File(options.getTmpFileDirectory().concat(keyFileName));
                crt = new File(options.getTmpFileDirectory().concat(crtFileName));
                FileUtils.copyToFile(new ClassPathResource(certFileName).getInputStream(), cert);
                FileUtils.copyToFile(new ClassPathResource(keyFileName).getInputStream(), key);
                FileUtils.copyToFile(new ClassPathResource(crtFileName).getInputStream(), crt);
            }
            if (!cert.exists()) {
                LOGGER.error("SSL certificate file load failed, not exists, file: {}", certPath);
                return;
            }
            if (!key.exists()) {
                LOGGER.error("SSL certificate file load failed, not exists, file: {}", keyPath);
                return;
            }
            if (!crt.exists()) {
                LOGGER.error("SSL certificate file load failed, not exists, file: {}", crtPath);
                return;
            }
            SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(cert, key)
                    .trustManager(crt);
            if (options.isClientAuthRequired()) {
                sslContextBuilder.clientAuth(ClientAuth.REQUIRE);
            } else {
                sslContextBuilder.clientAuth(ClientAuth.NONE);
            }
            this.sslContext = sslContextBuilder.build();
        } catch (Exception e) {
            LOGGER.error("SSL context build failed", e);
        }
    }





}
