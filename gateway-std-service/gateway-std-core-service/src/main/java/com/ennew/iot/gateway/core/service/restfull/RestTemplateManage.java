package com.ennew.iot.gateway.core.service.restfull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:56 2023/5/25
 */
@Component
@AllArgsConstructor
@Slf4j
public class RestTemplateManage  implements IRestTemplateManage {

    private final RestTemplate restTemplate;


    @Override
    public <T> T sendGet(RequestParams<Map<String, String>> requestParams, Class<T> tClass, Map<String, String> header) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        HttpEntity<MultiValueMap<String, String>> httpEntity = restTemplateRequest.buildGetRequest(requestParams, header);
        log.info("sendGet url {}", requestParams.getRequestUrl());
        //拼装参数
        StringBuilder urlBuilder = new StringBuilder(requestParams.getRequestUrl())
                .append("?");
        Map<String, String> params = requestParams.getParams();
        for (Map.Entry<String, String> entry: params.entrySet()){
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        ResponseEntity<T> responseEntity = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, httpEntity, tClass);
        log.info("sendGet responseCode {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }

    @Override
    public <T> T sendGet(RequestParams<Map<String, String>> requestParams, Class<T> tClass) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        HttpEntity<MultiValueMap<String, String>> httpEntity = restTemplateRequest.buildFormPostRequest(requestParams,new HashMap<>());
        log.info("url {}", requestParams.getRequestUrl());
        ResponseEntity<T> responseEntity = restTemplate.exchange(requestParams.getRequestUrl(), HttpMethod.GET, httpEntity, tClass);
        log.info("responseCode {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }

    @Override
    public <T> T sendPostForForm(RequestParams<Map<String, String>> requestParams, Class<T> tClass) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        HttpEntity<MultiValueMap<String, String>> httpEntity = restTemplateRequest.buildFormPostRequest(requestParams,new HashMap<>());
        log.info("url {}", requestParams.getRequestUrl());
        log.info("params {}", requestParams);
        String url = requestParams.getRequestUrl();
        if (!StringUtils.isEmpty(requestParams.getRequestPath())) {
            url = String.format("%s/%s", requestParams.getRequestUrl(), requestParams.getRequestPath() == null);
        }

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, tClass);
        log.info("responseCode {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }

    @Override
    public <T> T sendPostForForm(RequestParams<Map<String, String>> requestParams, Class<T> tClass, Map<String, String> header) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        HttpEntity<MultiValueMap<String, String>> httpEntity = restTemplateRequest.buildFormPostRequest(requestParams,header);
        log.info("url {}", requestParams.getRequestUrl());
        log.info("params {}", requestParams);
        String url = requestParams.getRequestUrl();
        if (!StringUtils.isEmpty(requestParams.getRequestPath())) {
            url = String.format("%s/%s", requestParams.getRequestUrl(), requestParams.getRequestPath() == null);
        }

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, tClass);
        log.info("responseCode {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }

    @Override
    public <T> T sendPostForJson(RequestParams<?> requestParams, Class<T> tClass) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        RequestEntity<?> requestEntity = restTemplateRequest.buildPostRequest(requestParams);
        ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, tClass);
        log.info("responseCode platform {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }

    @Override
    public <T> T sendPostForJson(RequestParams<?> requestParams, Class<T> tClass, Map<String, String> header) {
        RestTemplateRequest restTemplateRequest = new RestTemplateRequest();
        RequestEntity<?> requestEntity = restTemplateRequest.buildJsonPostRequest(requestParams,header);
        ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, tClass);
        log.info("responseCode platform {}", responseEntity.getStatusCodeValue());
        return responseEntity.getBody();
    }
}
