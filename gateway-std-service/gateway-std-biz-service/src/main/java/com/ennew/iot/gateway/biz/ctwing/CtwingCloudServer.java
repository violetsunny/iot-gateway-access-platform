/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.ctwing;

/**
 * @author kanglele
 * @version $Id: CtwingCloudServer, v 0.1 2023/11/21 15:26 kanglele Exp $
 */
public interface CtwingCloudServer {

    void dealCloudData(String ctwingMessage) throws Exception;

}
