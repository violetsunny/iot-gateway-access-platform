/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.processor.job;

import cn.enncloud.iot.gateway.timer.annotation.EnnIotXxlJob;
import cn.enncloud.iot.gateway.timer.handler.EnnIotXxlJobHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.clouddocking.service.HttpCloudService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 云云对接
 *
 * @author kanglele
 * @version $Id: HttpCloudJob, v 0.1 2023/5/17 16:55 kanglele Exp $
 */
@Slf4j
@EnnIotXxlJob("HttpCloudJob")
@Component
public class HttpCloudJob  extends EnnIotXxlJobHandler {

    @Resource
    private HttpCloudService httpCloudService;

    @Override
    public boolean doExecute(String s) {
        log.info("------开始跑job啦-------------");
        try {
            if (StringUtils.isBlank(s) || (!s.startsWith("{") || !s.endsWith("}"))) {
                return true;
            }
//            String[] strings = StringUtils.split(s, ",");
//            if (ArrayUtils.isEmpty(strings) && strings.length < 1) {
//                return true;
//            }
//            String tenant = Arrays.stream(strings).findFirst().get();
            JSONObject jsonObject = JSON.parseObject(s);
            String tenant = jsonObject.getString("tenant");
            List<String> productIds = (List<String>) jsonObject.getObject("productIds", List.class);
            httpCloudService.executeWork(tenant, productIds);
        } catch (Exception e) {
            log.error("------job异常啦-------------", e);
            return false;
        }
        return true;
    }

//    public void execute() throws Exception {
//
//    }
}
