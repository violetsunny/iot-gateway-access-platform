/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.clouddocking.service;

import com.ennew.iot.gateway.core.message.CloudWorkDataMessage;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author kanglele
 * @version $Id: HttpCloudServer, v 0.1 2023/5/19 16:47 kanglele Exp $
 */
public interface HttpCloudService {

    /**
     * 执行
     * @param tenant
     * @param productIds
     */
    void executeWork(String tenant,List<String> productIds) throws Exception;

    /**
     * 处理云数据
     * @param message
     * @throws Exception
     */
    void dealCloudData(CloudWorkDataMessage message) throws Exception;
}
