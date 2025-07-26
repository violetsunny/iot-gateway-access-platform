package com.ennew.iot.gateway.biz.protocol.management.script;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.protocol.enums.ProtocolDirectionTypeEnum;
import com.ennew.iot.gateway.biz.protocol.enums.ProtocolProvideTypeEnum;
import com.ennew.iot.gateway.biz.protocol.service.impl.ProtocolSupportServiceImpl;
import com.ennew.iot.gateway.biz.protocol.supports.RenameProtocolSupport;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.message.codec.Transport;
import com.ennew.iot.gateway.client.protocol.CompositeProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.ProtocolSupportProvider;
import com.ennew.iot.gateway.client.protocol.model.*;
import com.ennew.iot.gateway.client.utils.SpringContextUtil;
import com.ennew.iot.gateway.core.bo.ProtocolSupportDefinition;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.hswebframework.expands.script.engine.DynamicScriptEngine;
import org.hswebframework.expands.script.engine.DynamicScriptEngineFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScriptProtocolSupportProvider implements ProtocolSupportProvider {

    private ProtocolSupportDefinition definition;

    private Function<byte[], Map<String, Object>> decoderFunc;
    private Function<Message, String> encoderFunc;

    public ScriptProtocolSupportProvider(ProtocolSupportDefinition definition) {
        this.definition = definition;
    }

    @Override
    @SneakyThrows
    public ProtocolSupport create(SpringContextUtil context) {
        CompositeProtocolSupport support = new CompositeProtocolSupport();
        support.setId(definition.getId());
        support.setName(definition.getName());
        support.setDescription(definition.getDescription());

        Map<String, Object> configs = definition.getConfiguration();
        String scriptContent = (String) configs.get("script");
        String lang = (String) configs.get("lang");
        if (StringUtils.isBlank(lang)) {
            lang = "js";
        }
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(lang);
        engine.compile("handle", scriptContent);
        Map<String, Object> ctx = Maps.newHashMap();
        ctx.put("codec", this);
        ctx.put("logger", LoggerFactory.getLogger("message.handler"));
        engine.execute("handle", ctx).getIfSuccess();

        {
            DeviceMessageCodec codec = new DeviceMessageCodec() {
                @Override
                public Transport getSupportTransport() {
                    return DefaultTransport.TCP;
                }

                @Override
                public Message parseFrom(byte[] messageBytes) {
                    Map<String, Object> msg = decoderFunc.apply(messageBytes);
                    MessageType messageType = MessageType.valueOf(msg.get("messageType").toString());
                    switch (messageType) {
                        case LOGIN_REQ:
                            return JSONObject.parseObject(JSONObject.toJSONString(msg), LoginRequest.class);
                        case REPORT_REQ:
                            return JSONObject.parseObject(JSONObject.toJSONString(msg), ReportRequest.class);
                        case OPERATION_RSP:
                            return JSONObject.parseObject(JSONObject.toJSONString(msg), OperationResponse.class);
                    }
                    return null;
                }

                @Override
                public byte[] toByteArray(Message message) {
                    String msg = encoderFunc.apply(message);
                    return msg.getBytes();
                }

                @Override
                public boolean login(LoginRequest loginRequest) {
                    return true;
                }
            };
            support.addMessageCodecSupport(DefaultTransport.TCP, codec);
        }

        return support;
    }

    @Override
    public void close() throws IOException {

    }

    public void decoder(Function<byte[], Map<String, Object>> decoderFunc) {
        this.decoderFunc = decoderFunc;
    }

    public void encoder(Function<Message, String> encoderFunc) {
        this.encoderFunc = encoderFunc;
    }

//    public static void main(String[] args) {


//        SpringContextUtil springContextUtil = new SpringContextUtil();
//        ProtocolSupportDefinition definition = new ProtocolSupportDefinition();
//        definition.setId("111");
//        definition.setName("test");
//        Map<String, Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("lang", "js");
//        objectObjectHashMap.put("script", "codec.decoder(function(byteArray){\n" +
//                "    var dataStr='';\n" +
//                "    for(var i=0;i<byteArray.length;i++){\n" +
//                "        dataStr+=String.fromCharCode(byteArray[i]);\n" +
//                "    }\n" +
//                "    var jsonObject=JSON.parse(dataStr);\n" +
//                "    jsonObject.transport='TCP';\n" +
//                "    logger.info('res:{}', JSON.stringify(jsonObject));\n" +
//                "    return jsonObject;});\n" +
//                "\n" +
//                "codec.encoder(function(msg){\n" +
//                "    var map={deviceId: msg.deviceId,\n" +
//                "             messageId: msg.messageId,\n" +
//                "             messageType: msg.messageType+'',\n" +
//                "             transport: msg.transport};\n" +
//                "    if(map.messageType=='LOGIN_RSP' || map.messageType=='REPORT_RSP'){\n" +
//                "        map.result=msg.result;map.timeStamp=msg.timeStamp}\n" +
//                "     var dataStr = JSON.stringify(map);\n" +
//                "    logger.info('dataStr:'+dataStr);\n" +
//                "    return dataStr;});");

//        objectObjectHashMap.put("script", "codec.decoder(function(byteArray){\n" +
//                "    var jsonStr = '';\n" +
//                "    for(var i=0;i<byteArray.length;i++){\n" +
//                "        jsonStr+=String.fromCharCode(byteArray[i]);\n" +
//                "    }\n" +
//                "    var result = {\n" +
//                "        deviceId: '',\n" +
//                "        timeStamp: 1,\n" +
//                "        ingestionTime: 1,\n" +
//                "        messageId: '11',\n" +
//                "        metrics: {data: '1'},\n" +
//                "        messageType: 'REPORT_REQ'\n" +
//                "    }\n" +
//                "    return result;\n" +
//                "});");
//        objectObjectHashMap.put("script", "codec.decodeMulti(function(byteArray,params){\n" +
//                "    var jsonStr = byteArray;\n" +
//                "   \n" +
//                "   var data = jsonStr.data;\n" +
//                "     $log.info(JSON.stringify(data));\n" +
//                "     $log.info(data);\n" +
//                "    var dev =$ctx.getDeviceIdBySn('1','1');\n" +
//                "    var result = {\n" +
//                "        deviceId: dev,\n" +
//                "        timeStamp: 1,\n" +
//                "        ingestionTime: 1,\n" +
//                "        messageId: '11',\n" +
//                "        metrics: [{ts: '1',\n" +
//                "                    code:'aaa',\n" +
//                "                    value: 20}],\n" +
//                "        messageType: 'DEVICE_REPORT_REQ'\n" +
//                "    }\n" +
//                "    var a =result.hasOwnProperty('deviceId')\n" +
//                "     $log.info(data);\n" +
//                "    $log.info(JSON.stringify(byteArray))\n" +
//                "    return data;\n" +
//                "});\n");

//
//        String a ="codec.decodeMulti(function(str, params){\n" +
//            "    var data = JSON.parse(str);\n" +
//                    "    var topic = params[0];\n" +
//                    "    var topics = {\n" +
//                    "        'rtg': /\\/edge\\/single\\/(.+?)\\/(.+?)\\/rtg/\n" +
//                    "    };\n" +
//                    "    if(topics.rtg.test(topic)){\n" +
//                    "        var topicParams = topic.split('/');\n" +
//                    "        var pKey = topicParams[3];\n" +
//                    "        var sn = topicParams[4];\n" +
//                    "        // 查询设备ID\n" +
//                    "        // var deviceId = $ctx.getDeviceIdBySn(null, sn);\n" +
//                    "        var deviceId = \"11111\";\n" +
//                    "        $log.info('设备ID:' + deviceId);\n" +
//                    "        var ack = data.ack;\n" +
//                    "        var time = parseInt(data.ts);\n" +
//                    "        var metrics = [];\n" +
//                    "        var payload = data.payload;\n" +
//                    "        var ks = Object.getOwnPropertyNames(payload);\n" +
//                    "        var now = new Date().getTime();\n" +
//                    "        for(var i = 0; i < ks.length; i++){\n" +
//                    "            var key = ks[i];\n" +
//                    "            var value = payload[key];\n" +
//                    "            var mertic =  {code: key, value: value, ts: time};\n" +
//                    "            metrics.push(mertic);\n" +
//                    "        }\n" +
//                    "        // 构建Message对象\n" +
//                    "        var result = {\n" +
//                    "            deviceId: deviceId,\n" +
//                    "            timeStamp: time,\n" +
//                    "            ingestionTime: now,\n" +
//                    "            messageId: data.seq,\n" +
//                    "            metrics: metrics,\n" +
//                    "            messageType: 'DEVICE_REPORT_REQ'\n" +
//                    "        }\n" +
//                    "        if(ack === '1'){\n" +
//                    "            result.response = JSON.stringify({\n" +
//                    "                devtype: data.devtype,\n" +
//                    "                seq: data.seq,\n" +
//                    "                ts: now + '',\n" +
//                    "                code: '200'\n" +
//                    "            });\n" +
//                    "        }\n" +
//                    "        return JSON.stringify([result]);\n" +
//                    "    }else{\n" +
//                    "        $log.warn('暂不支持该主题数据，' + topic);\n" +
//                    "    }\n" +
//                    "    return [];\n" +
//                    "});";
//        String script = "codec.decodeMulti(function(byteArray,params) {\n" +
//                "     var dataStr = byteArray;\n" +
//                "    var jsonObject = JSON.parse(dataStr);\n" +
//                "    \n" +
//                "    var req1 = jsonObject.data;\n" +
//                "    var sn = req1.ie;\n" +
//                "    var deviceId = $ctx.getDeviceIdBySn('', sn);\n" +
//                "    if (deviceId==null || deviceId==='') {\n" +
//                "        //没有对应的恩牛设备\n" +
//                "         $log.warn('{} 没有对应的恩牛设备',sn);\n" +
//                "        return null;\n" +
//                "    }\n" +
//                "    \n" +
//                "    var result ={};\n" +
//                "    result.deviceId=deviceId;\n" +
//                "    result.messageType='DEVICE_REPORT_REQ';\n" +
//                "    var timestamp = Date.now();\n" +
//                "    result.timeStamp=timestamp;\n" +
//                "    result.ingestionTime=timestamp;\n" +
//                "    \n" +
//                "    var metrics = [];\n" +
//                "    var datatime = Date.parse(req1.onlineTimeStr);\n" +
//                "    var data = {}\n" +
//                "    data =req1.data; $log.warn('res:{}', req1); \n" +
//                "    for (var key in data){\n" +
//                "          $log.warn('res:{}', key);if('online'.equals(key)){\n" +
//                "            metrics.push({ code: key, value: req1[key],ts: datatime});\n" +
//                "        } else  if(data.hasOwnProperty(key)){\n" +
//                "            metrics.push({ code: key, value: data[key],ts: datatime});\n" +
//                "        }\n" +
//                "    }\n" +
//                "    result.metrics =metrics;\n" +
//                "    $log.info('res:{}', JSON.stringify(result));\n" +
//                "    \n" +
//                "    var requests = [];\n" +
//                "    requests.push(result);\n" +
//                "    return JSON.stringify(requests);\n" +
//                "});";
//
//        String b ="codec.decodeMulti(function(dataStr,params){\n" +
//                "  \n" +
//                "    var jsonObject = JSON.parse(dataStr);\n" +
//                "     $log.info(\"脚本请求参数：\"+jsonObject);\n" +
//                "    var dev =$ctx.getDeviceIdBySn('1','1');\n" +
//                "    var result = {\n" +
//                "        deviceId: dev,\n" +
//                "        timeStamp: 1,\n" +
//                "        ingestionTime: 1,\n" +
//                "        messageId: '11',\n" +
//                "        metrics: [{ts: '1',\n" +
//                "                    code:'aaa',\n" +
//                "                    value: 20}],\n" +
//                "        messageType: 'DEVICE_REPORT_REQ'\n" +
//                "    }\n" +
//                "    var data = JSON.stringify(result.metrics);\n" +
//                "    $log.info(\"脚本请求参数：\"+jsonObject);\n" +
//                "    var res = $ctx.modelRefMap(\"ECEMS\",\"1792793659748687873\",\"1721716530784\",data);\n" +
//                " \n" +
//                "    $log.info(typeof res)\n" +
//                "   \n" +
//                "    var metricList = new Array();\n" +
//                "    \n" +
//                "    metricList[0]=JSON.parse(res)\n" +
//                "    \n" +
//                "    result.metrics = metricList;\n" +
//                "    $log.info(metricList)\n" +
//                "    return JSON.stringify(result);\n" +
//                "});\n";
//        objectObjectHashMap.put("script", b);
//        definition.setConfiguration(objectObjectHashMap);
//
//        System.out.println(JSONObject.toJSONString(definition));
//
//        String a1 = " {\n" +
//                "      \"data\": {\n" +
//                "        \"code\": 200,\n" +
//                "        \"message\": \"\",\n" +
//                "        \"data\": {\n" +
//                "            \"ie\": \"YB69XXXXXXXXXX\",\n" +
//                "            \"online\": true,\n" +
//                "            \"onlineTime\": \"2024-03-27 10:39:29\",\n" +
//                "            \"data\": {\n" +
//                "                \"type\": 0,\n" +
//                "                \"at\": \"30.4\",\n" +
//                "                \"ah\": \"57.8\",\n" +
//                "                \"mode\": 0,\n" +
//                "                \"ppos\": 92,\n" +
//                "                \"psize\": 120,\n" +
//                "                \"csq\": 25\n" +
//                "            },\n" +
//                "            \"onlineTimeStr\": \"2024-03-27 10:39:29\"\n" +
//                "        },\n" +
//                "        \"success\": true\n" +
//                "    },\n" +
//                "        \"params\": [ \"/aaaaa\"]\n" +
//                "        \n" +
//                "    }";
//        ProtocolSupportServiceImpl protocolSupportService = new ProtocolSupportServiceImpl();
//        String decode = protocolSupportService.runScriptParse(definition, ProtocolDirectionTypeEnum.UP.getCode(), a1);
//
//
//
//        System.out.println("执行结果：" + JSONUtil.toJsonPrettyStr(decode));


//    }
}
