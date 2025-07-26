///**
// * llkang.com Inc.
// * Copyright (c) 2010-2023 All Rights Reserved.
// */
//package com.ennew.iot.gateway.biz.gateway;
//
//import com.ennew.iot.gateway.biz.config.ServerConfig;
//import com.ennew.iot.gateway.biz.config.ServerConfigs;
//import com.ennew.iot.gateway.biz.queue.CacheQueue;
//import com.ennew.iot.gateway.biz.queue.DownDataTransfer;
//import com.ennew.iot.gateway.biz.queue.UpDataTransfer;
//import com.ennew.iot.gateway.biz.server.NettyServer;
//import com.ennew.iot.gateway.client.utils.SpringContextUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
///**
// * @author kanglele
// * @version $Id: TcpServerGateway, v 0.1 2023/2/3 11:27 kanglele Exp $
// */
//@Slf4j
//@Component
//public class TcpServerGateway {
//
//    @Resource
//    private SpringContextUtil context;
//
//    @PostConstruct
//    private void start(){
//        // 初始化配置
//        // 初始化缓冲队列
//        try {
//            new UpDataTransfer(CacheQueue.up2MQQueue, 1).start();
//            new DownDataTransfer(CacheQueue.down2GateQueue, 1).start();
//        } catch (Exception e) {
//            log.error("缓冲队列初始化失败！", e);
//            System.exit(-1);
//        }
//
//        ServerConfigs configs = new ServerConfigs();
//        String jar1 = "D:/java-code/iot/iot-gateway-std-tcp/gateway-protocol-json-demo/target/gateway-protocol-json-demo-1.0-SNAPSHOT.jar";
//        String jar2 = "D:/java-code/iot/iot-gateway-std-tcp/gateway-protocol-binary-demo/target/gateway-protocol-binary-demo-1.0-SNAPSHOT.jar";
//        configs.getServerConfigs().add(new ServerConfig("0.0.0.0", 36001, "cn.enncloud.iot.gateway.protocol.demo.JsonDemoProtocol", jar1));
//        configs.getServerConfigs().add(new ServerConfig("0.0.0.0", 36002, "cn.enncloud.iot.gateway.protocol.demo.BinaryDemoProtocol", jar2));
//
//
//        // 初始化Netty
//        NettyServer nettyServer = context.getBean(NettyServer.class);
//        nettyServer.setConfigs(configs);
//        nettyServer.start();
//
//        // {"deviceId":"123456","messageId":"54321","messageType":"LOGIN_REQ"}
//    }
//}
