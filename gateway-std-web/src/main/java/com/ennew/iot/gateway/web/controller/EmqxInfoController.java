package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.config.EmqxConfiguration;
import com.ennew.iot.gateway.biz.gateway.service.DeviceGatewayService;
import com.ennew.iot.gateway.web.vo.EmqxInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

@RequestMapping("/info")
@RestController
@Tag(name = "接入信息")
@Slf4j
public class EmqxInfoController {

    @Resource
    EmqxConfiguration configuration;

    @Autowired
    private DeviceGatewayService deviceGatewayService;

    @Resource
    Environment environment;

    @Operation(summary = "获取emqx连接信息")
    @GetMapping("/emqx")
    public SingleResponse<EmqxInfoVo> getEmqxInfo() {
        EmqxInfoVo vo = new EmqxInfoVo(configuration.getHost(), configuration.getPort(), false, configuration.getUserName(), configuration.getPassword());
        return SingleResponse.buildSuccess(vo);
    }

    @Operation(summary = "获取http连接信息")
    @GetMapping("/http")
    public SingleResponse<String> getHTTPInfo() {
        String ip = "localhost";
        String port = "8870";
        try {
            ip = getIP();
            port = environment.getProperty("server.port");
        } catch (Exception e) {
            log.warn("获取ip异常");
        }
        return SingleResponse.buildSuccess("http://" + ip + ":" + port + "/access/std/rtg");
    }

    @Operation(summary = "获取tcp连接信息")
    @GetMapping("/tcp")
    public MultiResponse<HashMap<String, String>> getTCPInfo() {
        return deviceGatewayService.listNetWorkConfig();
    }

    private String getIP() throws SocketException {
        String ipAddress = "";
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress inetAddress = addresses.nextElement();
                if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    ipAddress = inetAddress.getHostAddress();
                }
            }
        }
        return ipAddress;
    }

}








