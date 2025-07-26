package com.ennew.iot.gateway.starter.configuration;


import com.ennew.iot.gateway.biz.server.cluster.ClusterManager;
import com.ennew.iot.gateway.biz.server.cluster.ClusterOperator;
import com.ennew.iot.gateway.biz.server.cluster.ServerNode;
import com.ennew.iot.gateway.processor.job.ClusterAliveKeeper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 集群管理配置类
 * 定义了集群的实现方式(redis)
 * 集群名称、当前节点信息
 */
@Configuration
@Slf4j
public class ClusterConfig {
    @Autowired
    private Environment environment;
    @Value("${ennew.iot.cluster.name:iot-gateway-other}")
    private String clusterName;
    @Value("${ennew.iot.cluster.keepAliveTime:2000}")
    private long keepAliveTime;
    @Value("${ennew.iot.cluster.timeout:5000}")
    private long timeout;
    //可以起个名字 默认为地址:端口号
    private final String serverName = "";
    private String serverId;
    private String serverAddress;
    private final Map<String, Object> tags = new HashMap<>();

    @Bean(initMethod = "startup", destroyMethod = "shutdown")
    public ClusterManager clusterManager(ClusterOperator operator) {
        ServerNode current = new ServerNode(serverId, serverName, serverAddress, tags);
        return new ClusterManager(clusterName, timeout, current, operator);
    }

    @Bean(initMethod = "startup")
    public ClusterAliveKeeper clusterAliveKeeper(ClusterManager clusterManager) {
        return new ClusterAliveKeeper(clusterManager, 0, keepAliveTime);
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        serverAddress = getIpAddress();
        serverId = this.serverAddress + ":" + environment.getProperty("server.port");
        tags.put("address", this.serverId);
        tags.put("cpu", getCpuConfiguration());
        tags.put("memory", getMemConfiguration());
    }

    private String getCpuConfiguration() {
        return Runtime.getRuntime().availableProcessors() / 2 + "C";
    }

    private String getMemConfiguration() {
        OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return (int) Math.ceil(systemMXBean.getTotalPhysicalMemorySize() / (1024d * 1024 * 1024)) + "G";
    }

    private String getIpAddress() throws SocketException {
        String ip = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            String name = intf.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    //获得IP
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                            if (!"127.0.0.1".equals(ip)) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        }
        return ip;
    }
}
