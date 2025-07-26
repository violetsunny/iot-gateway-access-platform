/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.common.help;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.kdla.framework.common.utils.ObjectUtil;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author kanglele
 * @version $Id: VertxHttpUtil, v 0.1 2023/5/17 18:36 kanglele Exp $
 */
@Component
@Slf4j
public class VertxHttpHelp {

    @Resource
    private WebClient webClient;

    private static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * future.thenAccept(user -> {
     * System.out.println("Received user:\n" + user);
     * }).exceptionally(ex -> {
     * System.out.println("Something went wrong: " + ex.getMessage());
     * return null;
     * });
     *
     * @param url
     * @param headers
     * @param res
     * @param <T>
     * @return
     */
    public <T> CompletableFuture<T> getJson(String url, MultiMap headers, Class<T> res) {
        CompletableFuture<T> future = new CompletableFuture<>();
        webClient.getAbs(url)
                .putHeaders(headers)
                .putHeader("content-type", CONTENT_TYPE_JSON)
                .send(ar -> {
                    if (ar.succeeded()) {
                        HttpResponse<Buffer> response = ar.result();
                        future.complete(response.bodyAsJsonObject().mapTo(res));
                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });
        return future;
    }

    public <T> CompletableFuture<T> postJson(String url, MultiMap headers, Object req, Class<T> res) {
        CompletableFuture<T> future = new CompletableFuture<>();
        webClient.postAbs(url)
                .putHeaders(headers)
                .putHeader("content-type", CONTENT_TYPE_JSON)
                .sendBuffer(Buffer.buffer(JSON.toJSONString(req)), ar -> {
                    if (ar.succeeded()) {
                        HttpResponse<Buffer> response = ar.result();
                        future.complete(response.bodyAsJsonObject().mapTo(res));
                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                });
        return future;
    }


    public <T> CompletableFuture<T> sendRequest(HttpMethod method, String url, Map<String, String> headers, Object req, Class<T> res) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Future<HttpResponse<Buffer>> responseFuture = createRequest(method, url, headers, req);
        responseFuture.onComplete(ar -> {
            if (ar.succeeded()) {
                try {
                    HttpResponse<Buffer> response = ar.result();
                    if (res.equals(String.class)) {
                        String result = response.bodyAsString(StandardCharsets.UTF_8.name());
                        future.complete((T) result);
                        log.info("sendRequest {}", result);
                    } else {
                        T result = response.bodyAsJson(res);//默认json返回
                        future.complete(result);
                        log.info("sendRequest {}", result);
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e.getCause());
                    throw new BizException(ErrorCode.FAIL.getCode(), "调用外部接口异常", e.getCause());
                }
            } else {
                future.completeExceptionally(ar.cause());
                throw new BizException(ErrorCode.FAIL.getCode(), "调用外部接口失败");
            }
        });
        return future;
    }

    /**
     * Get请求的参数，请直接在url后拼接，不走addQueryParam
     *
     * @param method
     * @param url
     * @param headers
     * @param req
     * @return
     */
    private Future<HttpResponse<Buffer>> createRequest(HttpMethod method, String url, Map<String, String> headers, Object req) {
        Map<String, String> headers2 = new HashMap<>();
        headers.forEach((k, v) -> {
            headers2.put(k.toLowerCase(), v);
            headers2.putIfAbsent(HttpHeaderNames.CONTENT_TYPE.toString(), CONTENT_TYPE_JSON);
        });

        HttpRequest<Buffer> request = null;
        //HTTP method
        if (HttpMethod.GET.equals(method)) {
            request = webClient.getAbs(url);
        } else if (HttpMethod.POST.equals(method)) {
            request = webClient.postAbs(url);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        //HTTP head
        request.putHeaders(HeadersMultiMap.httpHeaders().setAll(headers));
        //HTTP head type responseFuture
        Future<HttpResponse<Buffer>> responseFuture;
        if (req != null) {
            if (headers2.get(HttpHeaderNames.CONTENT_TYPE.toString()).equalsIgnoreCase(HttpHeaderValues.APPLICATION_JSON.toString())) {
                if (req instanceof String) {
                    responseFuture = request.sendBuffer(Buffer.buffer((String) req));
                } else {
                    responseFuture = request.sendJson(req);
                }
            } else if (headers2.get(HttpHeaderNames.CONTENT_TYPE.toString()).equalsIgnoreCase(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                responseFuture = request.sendForm((MultiMap) req);
            } else if (headers2.get(HttpHeaderNames.CONTENT_TYPE.toString()).equalsIgnoreCase(HttpHeaderValues.MULTIPART_FORM_DATA.toString())) {
                responseFuture = request.sendMultipartForm((MultipartForm) req);
            } else {
                byte[] data = ObjectUtil.ObjectToByte(req);
                responseFuture = request.sendBuffer(Buffer.buffer(data));
            }
        } else {
            responseFuture = request.send();
        }

        return responseFuture;
    }

}
