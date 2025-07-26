package com.ennew.iot.gateway.biz.ctwing;

import com.ctiot.aep.mqmsgpush.sdk.IMsgConsumer;
import com.ctiot.aep.mqmsgpush.sdk.IMsgListener;
import com.ctiot.aep.mqmsgpush.sdk.MqMsgConsumer;
import com.ennew.iot.gateway.core.es.ElasticSearchOperation;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/**
 * Aep MQ消息推送接收Demo
 */
@Slf4j
@Component
public class CtwingMq {

    private IMsgConsumer consumer;
    @Resource
    private ExecutorService executorService;
    @Resource
    private CtwingCloudServer ctwingCloudServer;
    @Resource
    private ElasticSearchOperation elasticSearchOperation;


    @Value("${ennew.ctwing.server:1}")
    private String server;
    @Value("${ennew.ctwing.tenantId:1}")
    private String tenantId;
    @Value("${ennew.ctwing.token:1}")
    private String token;
    @Value("${ennew.ctwing.topic:1}")
    private String topic;

//    @PostConstruct
    public void consumer() {
        //TODO 测试
//        String server = "2000102221.mq-msgpush.ctwing.cn:16651"; //消息服务地址请在控制台-消息流转-目的地管理-MQ页面中查看
//        String tenantId = "2000102221";//租户ID
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDAwMTAyMjIxIn0.oNOskexFgNdk0-3bojBuOtLrvzDCyG-f9fI21_fW2fU";//身份认证token串
//        String topic = "enn_data_iot_metric";
        String certFilePath = ""; //直接填空字符串，CA证书，JDK已经内置相关根证书，无需指定

        //创建消息接收类
        consumer = new MqMsgConsumer();
        try {
            //创建消息接收Listener
            IMsgListener msgListener = msg -> {
                //接收消息
                log.info("电信云接受消息：{}", msg);
                executorService.execute(() -> {
                    //消息处理...
                    //为了提高效率，建议对消息进行异步处理（使用其它线程、发送到Kafka等）
                    try {
                        ctwingCloudServer.dealCloudData(msg);
                    } catch (Exception e) {
                        log.warn("电信云执行失败", e);
                    }
                });
                executorService.execute(() -> elasticSearchOperation.saveOriginalDeviceReport(msg));
            };
            //初始化
            /**
             * @param server  消息服务server地址
             * @param tenantId 租户Id
             * @param token    用户认证token
             * @param certFilePath 证书文件路径
             * @param topicNames   主题名列表，如果该列表为空或null，则自动消费该租户所有主题消息
             * @param msgListener 消息接收者
             * @return 是否初始化成功
             */
            String[] topics = StringUtils.split(topic, ",");
            consumer.init(server, tenantId, token, certFilePath, Lists.newArrayList(topics), msgListener);

            //开始接收消息
            consumer.start();

        } catch (Exception e) {
            log.warn("电信云消费消息失败", e);
        }

    }

    @PreDestroy
    public void close() {
        //程序退出时，停止接收、销毁
        if (consumer != null) {
            consumer.stop();
            consumer.destroy();
        }
    }

}
