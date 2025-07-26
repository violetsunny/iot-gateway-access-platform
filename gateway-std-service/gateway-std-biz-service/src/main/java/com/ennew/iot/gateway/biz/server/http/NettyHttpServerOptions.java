package com.ennew.iot.gateway.biz.server.http;

import lombok.Data;
import lombok.ToString;


/**
 * Netty HTTP Server 配置
 */
@Data
@ToString
public class NettyHttpServerOptions {

    private static final boolean DEFAULT_SSL_ENABLED = false;

    private static final String DEFAULT_HOST = "0.0.0.0";

    private static final boolean DEFAULT_ASYNC_HANDLER_ENABLED = false;

    private static final int DEFAULT_MAX_CONTENT_LENGTH = 512 * 1024;

    private static final String DEFAULT_TMP_FILE_DIRECTORY = "tmp/";

    private static final int DEFAULT_SO_BACKLOG = 128;

    /**
     * tcp so_backlog
     */
    private int soBacklog = DEFAULT_SO_BACKLOG;

    /**
     * enable SSL/TLS
     */
    private boolean sslEnabled = DEFAULT_SSL_ENABLED;

    /**
     * http server host
     */
    private String host = DEFAULT_HOST;

    /**
     * http server port
     */
    private int port;

    /**
     * 启用异步业务Handler
     */
    private boolean asyncHandlerEnabled = DEFAULT_ASYNC_HANDLER_ENABLED;

    /**
     * netty bossGroup 线程数
     */
    private int bossGroupThreadCount;

    /**
     * netty workerGroup 线程数
     */
    private int workerGroupThreadCount;


    /**
     * handler现称池线程数
     */
    private int asyncHandlerThreadCount;


    /**
     * 证书
     */
    private String sslCertPath;

    /**
     * 私钥
     */
    private String sslKeyPath;

    /**
     * CA
     */
    private String sslCrtPath;


    /**
     * 双向认证
     */
    private boolean clientAuthRequired = true;


    /**
     * HTTP body 最大长度
     */
    private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;


    /**
     * 临时文件目录
     */
    private String tmpFileDirectory = DEFAULT_TMP_FILE_DIRECTORY;
}
