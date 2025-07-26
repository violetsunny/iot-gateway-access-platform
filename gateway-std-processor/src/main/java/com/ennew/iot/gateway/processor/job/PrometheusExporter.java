//package com.ennew.iot.gateway.processor.job;
//
//import io.prometheus.client.Collector;
//import io.prometheus.client.exporter.HTTPServer;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//
///**
// * 应用测点暴露给Prometheus的端口
// **/
//@Component
//public class PrometheusExporter implements CommandLineRunner {
//
//    @Override
//    public void run(String... args) throws Exception {
//        //在此开启一个http端口，暴露给Prometheus调用
//        HTTPServer server = new HTTPServer(8869);
//        //通过collector类下的register方法可以把测点注册到上端口中
//        //Collector是测点集合，也可以同时有Counter等单独测点
//        //在需要埋点的类里定义如下
//        //Counter sampleCollect = Counter.build().name("").help("").labelNames("").register();
//    }
//
//
//}
