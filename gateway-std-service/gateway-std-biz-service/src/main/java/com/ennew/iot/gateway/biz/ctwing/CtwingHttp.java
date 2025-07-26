package com.ennew.iot.gateway.biz.ctwing;

import cn.hutool.core.lang.Assert;
import com.ennew.iot.gateway.biz.server.http.NettyHttpRequest;
import com.ennew.iot.gateway.biz.server.http.NettyHttpRequestHandler;
import com.ennew.iot.gateway.biz.server.http.NettyHttpResponse;
import com.ennew.iot.gateway.core.es.ElasticSearchOperation;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CtwingHttp implements NettyHttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtwingHttp.class);

    private final CtwingCloudServer ctwingCloudServer;

    private final ElasticSearchOperation elasticSearchOperation;


    @Autowired
    public CtwingHttp(CtwingCloudServer ctwingCloudServer, ElasticSearchOperation elasticSearchOperation) {
        Assert.notNull(ctwingCloudServer, "component ctwingCloudServer not init");
        Assert.notNull(elasticSearchOperation, "component elasticSearchOperation not init");
        this.ctwingCloudServer = ctwingCloudServer;
        this.elasticSearchOperation = elasticSearchOperation;
    }

    @Override
    public void handle(NettyHttpRequest request, NettyHttpResponse response) {
        if(request.content() == null || request.content().isEmpty()){
            LOGGER.error("CTWing request content is empty");
            response.sendOk(NettyHttpResponse.EMPTY_BODY);
            return;
        }
        try {
            String content = request.content();
            elasticSearchOperation.saveOriginalDeviceReport(content);
            ctwingCloudServer.dealCloudData(content);
            response.sendOk(NettyHttpResponse.EMPTY_BODY);
        } catch (Exception e) {
            LOGGER.warn("电信云执行失败", e);
            response.send(HttpResponseStatus.INTERNAL_SERVER_ERROR, NettyHttpResponse.EMPTY_BODY);
        }
    }

}
