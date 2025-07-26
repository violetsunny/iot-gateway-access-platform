/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.common.help;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author kanglele
 * @version $Id: CommonTest, v 0.1 2023/5/31 12:06 kanglele Exp $
 */
@Component
@Slf4j
public class CommonTest implements CommandLineRunner {

//    @Resource
//    private VertxHttpHelp vertxHttpHelp;

    @Override
    public void run(String... args) throws Exception {
//        Map heard = new HashMap();
//        heard.put("Authorization","Bearer fdd3ce42-84b6-46fb-9b5f-7d42e950ee67");
//        heard.put("Content-Type","application/json");
//        String json = " {\n" +
//                "                \"openId\": \"664b955c6b2644b49ed0dd305b91b51f\",\n" +
//                "                \"deviceId\": \"221810010403\"\n" +
//                "            }";
//        CompletableFuture<JSONObject> future = vertxHttpHelp.sendRequest(HttpMethod.POST, "https://iot-icome-ai-op.fat.ennew.com/test/dept?account=t-kanglele", heard, json, JSONObject.class);
//        JSONObject obj = future.get();
//        log.info(obj.toJSONString());
    }
}
