package com.ennew.iot.gateway.biz.clouddocking.manage;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ennew.iot.gateway.client.enums.CloudDockingParamsType;
import com.ennew.iot.gateway.client.enums.CloudDockingType;
import com.ennew.iot.gateway.core.bo.CloudDockingAuthBO;
import com.ennew.iot.gateway.core.bo.CloudDockingAuthParamsBO;
import com.ennew.iot.gateway.core.bo.CloudDockingResBO;
import com.ennew.iot.gateway.core.service.restfull.RequestParams;
import com.ennew.iot.gateway.core.service.restfull.RestTemplateManage;
import com.ennew.iot.gateway.core.service.restfull.RestTemplateRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;
import top.kdla.framework.supplement.http.VertxHttpClient;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author: alec
 * Description: 查询token
 * @date: 下午2:43 2023/5/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CloudDockingAuthManage {

    @Resource
    private VertxHttpClient vertxHttpClient;

    private final RestTemplateManage restTemplateManage;

    @SneakyThrows
    public JSONObject sendPostRequest(CloudDockingResBO cloudDockingResBO,CloudDockingAuthBO cloudDockingAuthBO, List<CloudDockingAuthParamsBO> dockingAuthParamsList) {
        RequestParams<Map<String, String>> requestParams = buildRequestEntity(cloudDockingResBO,cloudDockingAuthBO,dockingAuthParamsList);
        Map<String, String> header = getHeader(dockingAuthParamsList);

        String response;
        if (cloudDockingAuthBO.getRequestType().equals(CloudDockingType.RequestType.FORM.getCode())) {
            RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
            HttpEntity<MultiValueMap<String, String>> httpEntity = restTemplateRequest.buildFormPostRequest(requestParams,header);
            CompletableFuture<String> future = vertxHttpClient.sendRequest(HttpMethod.POST, requestParams.getRequestUrl(), httpEntity.getHeaders().toSingleValueMap(), httpEntity.getBody(), String.class);
            response = future.get();
            //response = restTemplateManage.sendPostForForm(requestParams,String.class, header);
        } else {
            CompletableFuture<String> future = vertxHttpClient.sendRequest(HttpMethod.POST, requestParams.getRequestUrl(), header, requestParams.getParams(), String.class);
            response = future.get();
            //response = restTemplateManage.sendPostForJson(requestParams,String.class, header);
        }
        return getResponseJson(cloudDockingAuthBO,response);
    }



    @SneakyThrows
    public JSONObject sendGetRequest(CloudDockingResBO cloudDockingResBO,CloudDockingAuthBO cloudDockingAuthBO, List<CloudDockingAuthParamsBO> dockingAuthParamsList) {
        /*请求参数*/
        RequestParams<Map<String, String>> requestParams = buildRequestEntity(cloudDockingResBO,cloudDockingAuthBO,dockingAuthParamsList);
        /*请求头*/
        Map<String, String> header = getHeader(dockingAuthParamsList);
        /*根据请求类型请求数据*/
        Map<String, String> params = requestParams.getParams();
        CompletableFuture<String> future = vertxHttpClient.sendRequest(HttpMethod.GET, requestParams.getRequestUrl(), header, params, String.class);
        String obj = future.get();
        String response = restTemplateManage.sendGet(requestParams,String.class,header);
        if (StringUtils.isEmpty(response)) {
            log.error("response is null");
            throw new BizException(ErrorCode.BIZ_ERROR);
        }
        return getResponseJson(cloudDockingAuthBO,response);
    }


    private JSONObject getResponseJson(CloudDockingAuthBO cloudDockingAuthBO,String response) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (StringUtils.isEmpty(cloudDockingAuthBO.getRootPath())){
                return jsonObject;
            }
            return jsonObject.getJSONObject(cloudDockingAuthBO.getRootPath());
        } catch (Exception e) {
            log.error("response is error", e);
            throw new BizException(ErrorCode.BIZ_ERROR);
        }
    }

    private RequestParams<Map<String, String>> buildRequestEntity(CloudDockingResBO cloudDockingResBO,CloudDockingAuthBO cloudDockingAuthBO, List<CloudDockingAuthParamsBO> dockingAuthParamsList) {
        RequestParams<Map<String, String>> requestParams = new RequestParams<>();
        String url = String.format("%s%s", cloudDockingResBO.getBaseUrl(), cloudDockingAuthBO.getRequestUrl());
        requestParams.setRequestUrl(url);
        Map<String, String> params = new HashMap<>();
        List<CloudDockingAuthParamsBO> paramsList = dockingAuthParamsList.stream().filter(res -> !res.getParamType().equals(CloudDockingParamsType.HEADER.getCode())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(paramsList)) {
            params.putAll(paramsList.stream().collect(Collectors.toMap(CloudDockingAuthParamsBO::getParamKey, CloudDockingAuthParamsBO::getParamValue)));
        }
        if (params.size() > 0) {
            requestParams.setParams(params);
        }
        return requestParams;
    }

    private Map<String, String> getHeader(List<CloudDockingAuthParamsBO> dockingAuthParamsList) {

        List<CloudDockingAuthParamsBO> paramsList = dockingAuthParamsList.stream().filter(res -> res.getParamType().equals(CloudDockingParamsType.HEADER.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(paramsList)) {
            return new HashMap<>();
        }
        return paramsList.stream().collect(Collectors.toMap(CloudDockingAuthParamsBO::getParamKey, CloudDockingAuthParamsBO::getParamValue));
    }


    private JSONObject getChildrenJson(List<String> key, JSONObject jsonObject) {

        if (Objects.isNull(jsonObject.getJSONObject(key.get(0)))) {
            return jsonObject;
        }
        JSONObject object = jsonObject.getJSONObject(key.get(0));
        if (key.size() == 1) {
            return object;
        }
        return getChildrenJson(key.subList(1, key.size()), object);
    }
}
