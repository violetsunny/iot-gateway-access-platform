package com.ennew.iot.gateway.core.service.restfull;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:57 2023/5/25
 */
@Slf4j
public class RestTemplateRequest implements IRestTemplateRequest  {

    @Override
    public HttpEntity<MultiValueMap<String, String>> buildGetRequest(RequestParams<Map<String, String>> requestParam, Map<String, String> header) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if (Objects.nonNull(requestParam.getParams())) {
            body.setAll(requestParam.getParams());
        }
        return new HttpEntity<>(body, httpHeaders);
    }

    @Override
    public HttpEntity<MultiValueMap<String, String>> buildFormPostRequest(RequestParams<Map<String, String>> requestParam, Map<String, String> header) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.setAll(requestParam.getParams());
        return new HttpEntity<>(body, httpHeaders);
    }

    @Override
    public <T> RequestEntity<T> buildPostRequest(RequestParams<T> requestParam) {
        URI uri = fromHttpUrl(requestParam.getRequestUrl()).path(requestParam.getRequestPath()).build().toUri();
        RequestEntity.BodyBuilder builder =
                RequestEntity.post(uri)
                        .contentType(APPLICATION_JSON);
        log.info("请求url {} - {}", uri.toString(), JSONUtil.toJsonStr(requestParam.getParams()));
        return builder.body(requestParam.getParams());
    }

    @Override
    public <T> RequestEntity<T> buildPostRequest(RequestParams<T> requestParam, Map<String, String> header) {
        RequestEntity.BodyBuilder builder = buildJsonBody(requestParam, header);
        return builder.body(requestParam.getParams());
    }

    public <T> RequestEntity<JSONObject> buildJsonPostRequest(RequestParams<T> requestParam, Map<String, String> header) {
        RequestEntity.BodyBuilder builder = buildJsonBody(requestParam, header);
        return builder.body(JSONUtil.parseObj(requestParam.getParams()));
    }


    private RequestEntity.BodyBuilder  buildJsonBody(RequestParams<?> requestParam, Map<String, String> header) {
        URI uri = fromHttpUrl(requestParam.getRequestUrl()).path(requestParam.getRequestPath()).build().toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }
        return RequestEntity.post(uri)
                .headers(httpHeaders)
                .contentType(APPLICATION_JSON);
    }
}
