/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.server;

import com.ennew.iot.gateway.core.bo.KeyValueBo;

import java.util.List;

/**
 * @author kanglele
 * @version $Id: CommonServer, v 0.1 2023/11/15 15:32 kanglele Exp $
 */
public interface CommonServer {

    List<KeyValueBo<String>> netWork();

}
