package com.ennew.iot.gateway.core.service.restfull;

import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:55 2023/5/25
 */
public interface IRestTemplateRequest {

    /**
     * 构建request请求参数
     * @param requestParam 请求参数泛型
     * @param header 请求头
     * @return 返回请求参数
     * */
    HttpEntity<MultiValueMap<String, String>> buildGetRequest(RequestParams<Map<String, String>> requestParam, Map<String, String> header);

    /**
     * 构建request请求参数
     * @param requestParam 请求参数泛型
     * @param header 请求头
     * @return 返回请求参数
     * */
    HttpEntity<MultiValueMap<String, String>> buildFormPostRequest(RequestParams<Map<String, String>> requestParam, Map<String, String> header);


    /**
     * 构建request请求参数
     * @param requestParam 请求参数泛型
     * @return 返回请求参数
     * */
    <T> RequestEntity<T> buildPostRequest(RequestParams<T> requestParam);

    /**
     * 构建request请求参数
     * @param requestParam 请求参数泛型
     * @param header 请求头
     * @return 返回请求参数
     * */
    <T> RequestEntity<T> buildPostRequest(RequestParams<T> requestParam,Map<String, String> header);
}
