package com.ennew.iot.gateway.biz.protocol.management.jar;

import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.protocol.management.ProtocolSupportLoaderProvider;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupportProvider;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.kdla.framework.exception.BizException;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JarProtocolSupportLoader implements ProtocolSupportLoaderProvider {

    @Autowired
    private SpringContextUtil serviceContext;

    private final Map<String, ProtocolClassLoader> protocolLoaders = new ConcurrentHashMap<>();

    private final Map<String, ProtocolSupportProvider> loaded = new ConcurrentHashMap<>();

    @Override
    public String getProvider() {
        return "jar";
    }

    protected ProtocolClassLoader createClassLoader(URL location) {
        return new ProtocolClassLoader(new URL[]{location}, this.getClass().getClassLoader());
    }

    @SneakyThrows
    protected void closeLoader(ProtocolClassLoader loader) {
        loader.close();
    }

    @Override
    @SneakyThrows
    public ProtocolSupport load(ProtocolSupportDefinition definition) {
        String id = definition.getId();
        Map<String, Object> config = definition.getConfiguration();
        String location = Optional
                .ofNullable(config.get("location"))
                .map(String::valueOf)
                .orElseThrow(() -> new BizException("configuration中没有location信息"));

        URL url;

        if (!location.contains("://")) {
            File file = new File(location);
            if (file.exists()) {
                url = file.toURI().toURL();
            } else {
                throw new BizException("File does not exist:" + location);
            }
        } else {
            try {
                url = new URL("jar:" + location + "!/");
                url.openConnection().connect();
            } catch (Exception e) {
                throw new BizException("url File does not exist:" + location);
            }
        }
        ProtocolClassLoader loader;
        URL fLocation = url;
        {
            ProtocolSupportProvider oldProvider = loaded.remove(id);
            if (null != oldProvider) {
                oldProvider.close();
            }
        }
        loader = protocolLoaders.compute(id, (key, old) -> {
            if (null != old) {
                try {
                    closeLoader(old);
                } catch (Exception ignore) {

                }
            }
            return createClassLoader(fLocation);
        });
        ProtocolSupportProvider supportProvider;
        log.debug("load protocol support from : {}", location);
        String provider = Optional
                .ofNullable(config.get("provider"))
                .map(String::valueOf)
                .map(String::trim)
                .orElse(null);
        if (provider != null) {
            //直接从classLoad获取,防止冲突
            @SuppressWarnings("all")
            Class<ProtocolSupportProvider> providerType = (Class) loader.loadSelfClass(provider);
            supportProvider = providerType.getDeclaredConstructor().newInstance();
        } else {
            supportProvider = lookupProvider(loader);
            if (null == supportProvider) {
                throw new BizException("没找到support provider");
            }
        }
        ProtocolSupportProvider oldProvider = loaded.put(id, supportProvider);
        try {
            if (null != oldProvider) {
                oldProvider.close();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return supportProvider.create(serviceContext);
    }

    protected ProtocolSupportProvider lookupProvider(ProtocolClassLoader classLoader) {

        return ClassUtils
                .findImplClass(ProtocolSupportProvider.class,
                        "classpath:**/*.class",
                        true,
                        classLoader,
                        ProtocolClassLoader::loadSelfClass)
                .orElse(null);
    }

    public static void main(String[] args) {
        ProtocolSupportDefinition definition = new ProtocolSupportDefinition();
        definition.setId("111");
        definition.setName("test");

//        String config = "{\n" +
//                "  \"provider\": \"com.ennew.iot.gateway.protocol.HttpProtocolSupportProvider\",\n" +
//                "  \"location\": \"D:/enn/iot-protocols/gateway-protocol-json-demo/target/gateway-protocol-json-demo-1.0-SNAPSHOT.jar\"\n" +
//                " \n" +
//                "}";
//        String config = "{\n" +
//                "  \"provider\": \"com.ennewiot.gateway.protocol.NbIndustrialAlarmProtocolSupportProvider\",\n" +
//                "  \"location\": \"D:/enn/iot-protocols/ctwing-industrialalarm-nb/target/ctwing-industrialalarm-nb-1.0-SNAPSHOT.jar\"\n" +
//                " \n" +
//                "}";
//
        String config = "{\"provider\":\"com.ennewiot.gateway.protocol.NbAlarmProtocolProvider\",\"location\":\"https://minio-iot.ennew.com/minio/protocol-test/202311274274a9b9b03c45a7951d1f2f9d1d5970.jar\"}";
        definition.setConfiguration(JSONObject.parseObject(config));
        JarProtocolSupportLoader jarProtocolSupportLoader = new JarProtocolSupportLoader();
        ProtocolSupport load = jarProtocolSupportLoader.load(definition);

        DeviceMessageCodec messageCodec = load.getMessageCodec(DefaultTransport.HTTP);

//        ReportRequest reportRequest = new ReportRequest();
//
//        reportRequest.setMessageType(MessageType.REPORT_REQ);
//        reportRequest.setTimeStamp(System.currentTimeMillis());
//        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
//        jsonObject.put("a1", "sssss");
//        jsonObject.put("b1", "sssss222");
//        reportRequest.setMetric(jsonObject);

        String input = "{\n" +
                "  \"IMEI\": \"862806069360672\",\n" +
                "  \"IMSI\": \"undefined\",\n" +
                "  \"assocAssetId\": \"\",\n" +
                "  \"deviceId\": \"5abdcf285d4e462596853fa2340dd545\",\n" +
                "  \"deviceType\": \"\",\n" +
                "  \"messageType\": \"dataReport\",\n" +
                "  \"payload\": {\n" +
                "    \"APPdata\": \"Z0AVAWIVIP////8BIqABvOXnAKtQanZFCqzcWoxpSuVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7Lfp2hN3rD4ip/yYtR+a7GBkd5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urgWZ/n072WBP1IRcyer6/jZlcNfr+A9Y2h3pPu0tFAW2ivu0=\"\n" +
                "  },\n" +
                "  \"productId\": \"16996520\",\n" +
                "  \"protocol\": \"lwm2m\",\n" +
                "  \"serviceId\": \"\",\n" +
                "  \"tenantId\": \"2000102221\",\n" +
                "  \"timestamp\": 1700709413480,\n" +
                "  \"topic\": \"v1/up/ad\",\n" +
                "  \"upDataSN\": -1,\n" +
                "  \"upPacketSN\": -1\n" +
                "}";


        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setMessageType(MessageType.REPORT_REQ);
        reportRequest.setTimeStamp(System.currentTimeMillis());


        JSONObject jsonObject = JSONObject.parseObject(input);
        reportRequest.setMetric(jsonObject);

        List<? extends Message> decode = messageCodec.decode(JSONObject.toJSONBytes(reportRequest));


        System.out.println(decode);
    }
}
