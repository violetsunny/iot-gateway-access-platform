package com.ennew.iot.gateway.core.repository;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import com.ennew.iot.gateway.core.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProtocolSupportManager {

    public static final String CACHEKEY = "ennew__protocol_supports_std";
    public static final String PROTOCOL_TOPIC = "ennew_protocol_changed";

    @Autowired
    private RedisService redisService;

    public void save(ProtocolSupportDefinition definition) {
        redisService.putHash(CACHEKEY, definition.getId(), JSONObject.toJSONString(definition));
        redisService.publish(PROTOCOL_TOPIC, JSONObject.toJSONString(definition));
    }

    public void remove(String id) {
        redisService.deleteHash(CACHEKEY, id);
    }

    public List<ProtocolSupportDefinition> loadAll() {
        return redisService.getHashEntries(CACHEKEY).values().stream().map(definition -> JSONUtil.toBean(String.valueOf(definition), ProtocolSupportDefinition.class)).collect(Collectors.toList());
    }
}
