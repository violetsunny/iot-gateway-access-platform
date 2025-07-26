/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.message;

import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author kanglele
 * @version $Id: CloudDataBo, v 0.1 2023/5/23 15:53 kanglele Exp $
 */
@Data
public class CloudWorkDataMessage {
    //POST GET
    private String httpMethod;
    //请求URL
    private String url;
    //请求头
    private Map headers;
    //请求体
    private Object req;
    //返回根路径
    private String resRoot;
    //租户
    private String tenant;
    //映射关系
    List<? extends MetadataMapping> metadataMapping;
    //限流
    private Integer limit;
    //是否同组
    private String group;
}
