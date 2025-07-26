package com.ennew.iot.gateway.biz.gateway.tcp;

import com.ennew.iot.gateway.biz.gateway.DeviceGateway;
import com.ennew.iot.gateway.biz.gateway.DeviceGatewayProvider;
import com.ennew.iot.gateway.biz.gateway.supports.DeviceGatewayProperties;
import com.ennew.iot.gateway.biz.protocol.supports.ProtocolSupports;
import com.ennew.iot.gateway.biz.server.handler.DataReportHandler;
import com.ennew.iot.gateway.biz.server.handler.ExceptionHandler;
import com.ennew.iot.gateway.biz.server.handler.LoginHandler;
import com.ennew.iot.gateway.biz.session.SessionManger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class TcpServerDeviceGatewayProvider implements DeviceGatewayProvider {

    @Autowired
    private ProtocolSupports protocolSupports;

    @Autowired
    private SessionManger sessionManger;

    @Autowired
    private LoginHandler loginHandler;

    @Autowired
    private DataReportHandler dataReportHandler;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Override
    public String getId() {
        return "tcp_server";
    }

    @Override
    public DeviceGateway createDeviceGateway(DeviceGatewayProperties properties) {
//        TcpServerProperties config = BeanUtil.toBean(properties.getNetworkConfiguration(), TcpServerProperties.class);
        //todo 证书配置
//        createNetwork(config);

        String protocol = properties.getProtocol();
        Assert.notNull(protocol, "protocol不能为空");
        return new TcpServerDeviceGateway(properties, protocolSupports.getProtocol(protocol), sessionManger, loginHandler, dataReportHandler, exceptionHandler);
    }
}
