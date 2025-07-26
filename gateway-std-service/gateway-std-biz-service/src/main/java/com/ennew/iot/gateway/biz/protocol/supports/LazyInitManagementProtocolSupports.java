package com.ennew.iot.gateway.biz.protocol.supports;

import com.ennew.iot.gateway.biz.protocol.management.ProtocolSupportLoader;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import com.ennew.iot.gateway.core.repository.ProtocolSupportManager;
import com.ennew.iot.gateway.core.service.RedisService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Getter
@Setter
@Component
@Order(1)
public class LazyInitManagementProtocolSupports extends StaticProtocolSupports implements CommandLineRunner, MessageListener, InitializingBean {

//    public static final String PROTOCOL_TOPIC = "_protocol_changed";

    @Autowired
    private ProtocolSupportManager manager;

    @Autowired
    private ProtocolSupportLoader loader;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTopicRegistry registry;

    private Map<String, String> configProtocolIdMapping = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = redisService.getTemplate().getStringSerializer().deserialize(pattern);
        if (ProtocolSupportManager.PROTOCOL_TOPIC.equals(topic)) {
            Object deserialize = redisService.getTemplate().getValueSerializer().deserialize(message.getBody());
            System.out.println(deserialize);
//            init((ProtocolSupportDefinition) Objects.requireNonNull(redisService.getTemplate().getValueSerializer().deserialize(message.getBody())));
        }
    }

    public void init() {
        manager.loadAll().stream().filter(de -> de.getState() == 1).forEach(this::init);
    }

    public void init(ProtocolSupportDefinition definition) {
        if (definition.getState() != 1) {
            String protocol = configProtocolIdMapping.get(definition.getId());
            if (protocol != null) {
                log.debug("uninstall protocol:{}", definition);
                unRegister(protocol);
                return;
            }
        }
        String operation = definition.getState() != 1 ? "uninstall" : "install";
        Consumer<ProtocolSupport> consumer = definition.getState() != 1 ? this::unRegister : this::register;
        log.debug("{} protocol:{}", operation, definition);
        ProtocolSupport protocolSupport = loader.load(definition);
        log.debug("{} protocol[{}] success: {}", operation, definition.getId(), protocolSupport);
        configProtocolIdMapping.put(definition.getId(), protocolSupport.getId());
        consumer.accept(protocolSupport);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registry.register(this, new PatternTopic(ProtocolSupportManager.PROTOCOL_TOPIC));
    }

    @Override
    public void run(String... args) throws Exception {
//        init();

        String 啊 = "codec.decodeMulti(function(str, params){\n" +
                "    var data = JSON.parse(str);\n" +
                "    var topic = params[0];\n" +
                "    var topics = {\n" +
                "        'rtg': /\\/edge\\/single\\/(.+?)\\/(.+?)\\/rtg/\n" +
                "    };\n" +
                "    if(topics.rtg.test(topic)){\n" +
                "        var topicParams = topic.split('/');\n" +
                "        var pKey = topicParams[3];\n" +
                "        var sn = topicParams[4];\n" +
                "         var deviceId = $ctx.getDeviceIdBySn(null, sn);\n" +
                "        $log.info('设备ID:' + deviceId);\n" +
                "        var ack = data.ack;\n" +
                "        var time = parseInt(data.ts);\n" +
                "        var metrics = [];\n" +
                "        var payload = data.payload;\n" +
                "        var ks = Object.getOwnPropertyNames(payload);\n" +
                "        var now = new Date().getTime();\n" +
                "        for(var i = 0; i < ks.length; i++){\n" +
                "            var key = ks[i];\n" +
                "            var value = payload[key];\n" +
                "            var mertic =  {code: key, value: value, ts: time};\n" +
                "            metrics.push(mertic);\n" +
                "        }\n" +
                "        var result = {\n" +
                "            deviceId: deviceId,\n" +
                "            timeStamp: time,\n" +
                "            ingestionTime: now,\n" +
                "            messageId: data.seq,\n" +
                "            metrics: metrics,\n" +
                "            messageType: 'DEVICE_REPORT_REQ'\n" +
                "        }\n" +
                "        if(ack === '1'){\n" +
                "            result.response = JSON.stringify({\n" +
                "                devtype: data.devtype,\n" +
                "                seq: data.seq,\n" +
                "                ts: now + '',\n" +
                "                code: '200'\n" +
                "            });\n" +
                "        }\n" +
                "        return JSON.stringify([result]);\n" +
                "    }else{\n" +
                "        $log.warn('暂不支持该主题数据，' + topic);\n" +
                "    }\n" +
                "    return [];\n" +
                "});";
    }
}
