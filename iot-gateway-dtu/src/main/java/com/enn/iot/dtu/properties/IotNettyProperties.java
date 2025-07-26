package com.enn.iot.dtu.properties;

import io.netty.util.NettyRuntime;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ennew.netty")
@Data
public class IotNettyProperties {
    private static final Integer DEFAULT_WORKER_EVENT_LOOP_THREADS = NettyRuntime.availableProcessors() * 2;
    private static final Integer DEFAULT_NETTY_PORT = 22602;

    /**
     * 工作线程数量，默认：CPU核心数量 * 2
     */
    private Integer workerEventLoopThreads = DEFAULT_WORKER_EVENT_LOOP_THREADS;

    /**
     * 网络服务端口，监听的TCP端口
     */
    private Integer port = DEFAULT_NETTY_PORT;
}
