package com.ennew.iot.gateway.biz.config;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServerConfigs {
    List<ServerConfig> serverConfigs = Lists.newArrayList();
}