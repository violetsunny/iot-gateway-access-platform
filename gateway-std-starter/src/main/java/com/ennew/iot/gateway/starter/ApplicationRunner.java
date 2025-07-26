package com.ennew.iot.gateway.starter;

import com.enn.iot.dtu.server.IotServer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;

/**
 * starter
 *
 * @author anyone
 * @since Wed Jun 23 16:09:34 CST 2021
 */
@SpringBootApplication(scanBasePackages = {"top.rdfa", "com.ennew.iot.gateway", "com.enn.iot.dtu"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@MapperScan("com.ennew.iot.gateway.dal.mapper")
@EnableFeignClients(basePackages = {"com.ennew.iot.gateway"})
@EnableKafka
@EnableDiscoveryClient
@EnableRetry
public class ApplicationRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    private static final String SHUTDOWN_HOOK_THREAD_NAME = "iot-shutdown-hook";

    private final IotServer iotServer;

    public ApplicationRunner(IotServer iotServer) {
        this.iotServer = iotServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(new IotShutdownHookTask(), SHUTDOWN_HOOK_THREAD_NAME));
        iotServer.startup();
        logger.info("app 终于能 start ！！！");
    }

    private class IotShutdownHookTask implements Runnable {
        @Override
        public void run() {
            iotServer.shutdownGracefully();
            logger.info("app shutdown");
        }
    }
}
