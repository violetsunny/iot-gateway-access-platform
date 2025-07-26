package com.ennew.iot.gateway.core.service.restfull;

import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:54 2023/5/25
 */
public interface IRestTemplateManage {

    /**
     * 发送GET请求
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @param header 请求头
     * @return 返回结果
     * */

    <T> T sendGet(RequestParams<Map<String, String>> requestParams, Class<T> tClass, Map<String,String> header);

    /**
     * 发送GET请求
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @return 返回结果
     * */

    <T> T sendGet(RequestParams<Map<String, String>> requestParams, Class<T> tClass);

    /**
     * 发送表单请求
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @return 返回结果
     * */

    <T> T sendPostForForm(RequestParams<Map<String, String>> requestParams, Class<T> tClass);

    /**
     * 发送表单请求
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @param header 请求头
     * @return 返回结果
     * */

    <T> T sendPostForForm(RequestParams<Map<String, String>> requestParams, Class<T> tClass, Map<String,String> header);

    /**
     * 发送Post请求 json
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @return 返回结果
     * */
    <T> T sendPostForJson(RequestParams<?> requestParams, Class<T> tClass);

    /**
     * 发送Post请求 json
     * @param requestParams 请求参数
     * @param tClass 返回值类型
     * @param header 请求头
     * @return 返回结果
     * */
    <T> T sendPostForJson(RequestParams<?> requestParams, Class<T> tClass, Map<String,String> header);
}
