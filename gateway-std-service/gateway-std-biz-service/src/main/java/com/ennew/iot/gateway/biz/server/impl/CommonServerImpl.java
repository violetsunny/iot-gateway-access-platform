/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.server.impl;

import com.ennew.iot.gateway.biz.server.CommonServer;
import com.ennew.iot.gateway.common.enums.NetworkEnum;
import com.ennew.iot.gateway.core.bo.KeyValueBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kanglele
 * @version $Id: CommonServerImpl, v 0.1 2023/11/15 15:34 kanglele Exp $
 */
@Slf4j
@Service
public class CommonServerImpl implements CommonServer {

    @Override
    public List<KeyValueBo<String>> netWork() {
        return Stream.of(NetworkEnum.values()).map(e-> {
            KeyValueBo<String> enumResBO = new KeyValueBo<>();
            enumResBO.setKey(e.getCode());
            enumResBO.setValue(e.getDesc());
            return enumResBO;
        }).collect(Collectors.toList());
    }
}
