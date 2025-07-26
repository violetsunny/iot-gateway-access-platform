package com.ennew.iot.gateway.biz.protocol.supports;

import lombok.Getter;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisTopicRegistry {

    @Getter
    private final Map<PatternTopic, MessageListener> topicListenerMapping = new HashMap<>();

    public void register(MessageListener listener, PatternTopic... topics) {
        Arrays.asList(topics).forEach(topic -> topicListenerMapping.put(topic, listener));
    }
}
