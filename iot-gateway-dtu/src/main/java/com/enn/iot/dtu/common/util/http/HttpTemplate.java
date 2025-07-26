package com.enn.iot.dtu.common.util.http;

import com.enn.iot.dtu.common.util.json.JsonUtils;
import lombok.Builder;
import lombok.ToString;
import okhttp3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
//@ConditionalOnProperty(name = HttpProperties.PROPERTIES_KEY_ENABLED, havingValue = "true")
@Slf4j
public class HttpTemplate {

    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String DELETE = "DELETE";
    public final static String PATCH = "PATCH";

    private final static String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
    private final static String DEFAULT_METHOD = GET;
    private final static String DEFAULT_MEDIA_TYPE = "application/json";
    public  final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private OkHttpClient client;
    @Autowired
    private HttpProperties iotHttpProperties;

    @PostConstruct
    private void init() {
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(iotHttpProperties.getMaxIdleConnections(),
                        iotHttpProperties.getKeepAliveDuration(), TimeUnit.SECONDS))
                // 连接超时重连，其它场景不重连。
                .retryOnConnectionFailure(true).connectTimeout(iotHttpProperties.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(iotHttpProperties.getReadTimeout(), TimeUnit.SECONDS)
                .readTimeout(iotHttpProperties.getReadTimeout(), TimeUnit.SECONDS).build();
    }

    /**
     * GET请求
     *
     * @param url
     *            URL地址
     * @return
     */
    public String get(String url) throws IOException {
        try {
            long start = System.currentTimeMillis();
            String result = execute(OkHttp.builder().url(url).build());
            if (log.isTraceEnabled()) {
                long end = System.currentTimeMillis();
                log.trace("send get, url:{}, cost:{}", url, end - start);
            }
            return result;
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * GET请求
     *
     * @param url
     *            URL地址
     * @return
     */
    public String get(String url, String charset) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).responseCharset(charset).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url
     *            URL地址
     * @param queryMap
     *            查询参数
     * @return
     */
    public String get(String url, Map<String, String> queryMap) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).queryMap(queryMap).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url
     *            URL地址
     * @param headerMap
     *            查询参数
     * @return
     */
    public String getByHeaderMap(String url, Map<String, String> headerMap) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).headerMap(headerMap).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url
     *            URL地址
     * @param queryMap
     *            查询参数
     * @return
     */
    public String get(String url, Map<String, String> queryMap, String charset) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).queryMap(queryMap).responseCharset(charset).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * POST application/json
     *
     * @param url
     * @param obj
     * @return
     */
    public String postJson(String url, Object obj) throws IOException {
        return postJsonWithString(url, JsonUtils.writeValueAsString(obj));
    }

    public String postJson(String url, Object obj, Map<String, String> headerMap) throws IOException {
        return postJsonWithString(url, JsonUtils.writeValueAsString(obj), headerMap);
    }

    /**
     * POST application/json
     *
     * @param url
     * @param str
     * @return
     */
    public String postJsonWithString(String url, String str) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).method(POST).data(str)
                    .mediaType(DEFAULT_MEDIA_TYPE).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    public String postJsonWithString(String url, String str, Map<String, String> headerMap) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).method(POST).data(str)
                    .mediaType(DEFAULT_MEDIA_TYPE).headerMap(headerMap).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * PUT application/json
     *
     * @param url
     * @param obj
     * @return
     */
    public String putJson(String url, Object obj) throws IOException {
        return putJsonWithString(url, JsonUtils.writeValueAsString(obj));
    }

    /**
     * PUT application/json
     *
     * @param url
     * @param str
     * @return
     */
    public String putJsonWithString(String url, String str) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).method(PUT).data(str)
                    .mediaType(DEFAULT_MEDIA_TYPE).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * DELETE application/json
     *
     * @param url
     * @return
     */
    public String delete(String url, String str) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).method(DELETE).data(str)
                    .mediaType(DEFAULT_MEDIA_TYPE).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    public String postFormAndJson(String url, Map<String, String> formMap, Object object) throws IOException {
        String data = "";
        if (!CollectionUtils.isEmpty(formMap)) {
            data = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
        }
        try {
            return execute(OkHttp.builder().url(url).method(POST).data(JsonUtils.writeValueAsString(object))
                    .queryMap(formMap).mediaType(DEFAULT_MEDIA_TYPE).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * POST application/x-www-form-urlencoded
     *
     * @param url
     * @param formMap
     * @return
     */
    public String postForm(String url, Map<String, String> formMap) throws IOException {
        String data = "";
        if (!CollectionUtils.isEmpty(formMap)) {
            data = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
        }
        try {
            return execute(OkHttp.builder().url(url).method(POST).data(data)
                    .mediaType(APPLICATION_FORM_URLENCODED).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    private String post(String url, String data, String mediaType, String charset) throws IOException {
        try {
            return execute(OkHttp.builder().url(url).method(POST).data(data).mediaType(mediaType)
                    .responseCharset(charset).build());
        } catch (NoSuchMethodException ex) {
            log.error("method error", ex);
            return null;
        }
    }

    /**
     * 通用执行方法
     */
    private String execute(OkHttp okHttp) throws NoSuchMethodException, IOException {
        if (StringUtils.isBlank(okHttp.requestCharset)) {
            okHttp.requestCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.responseCharset)) {
            okHttp.responseCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.method)) {
            okHttp.method = DEFAULT_METHOD;
        }
        if (StringUtils.isBlank(okHttp.mediaType)) {
            okHttp.mediaType = DEFAULT_MEDIA_TYPE;
        }
        // 记录请求日志
        if (iotHttpProperties.getRequestLogEnabled() && log.isDebugEnabled()) {
            log.debug(okHttp.toString());
        }

        String url = okHttp.url;

        Request.Builder builder = new Request.Builder();

        if (!CollectionUtils.isEmpty(okHttp.queryMap)) {
            String queryParams = okHttp.queryMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", queryParams);
        }
        builder.url(url);

        if (!CollectionUtils.isEmpty(okHttp.headerMap)) {
            okHttp.headerMap.forEach(builder::addHeader);
        }

        String method = okHttp.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", okHttp.mediaType, okHttp.requestCharset);

        if (StringUtils.equals(method, GET)) {
            builder.get();
        } else if (ArrayUtils.contains(new String[] {POST, PUT, DELETE, PATCH}, method)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), okHttp.data);
            builder.method(method, requestBody);
        } else {
            throw new NoSuchMethodException(String.format("http method:%s not support!", method));
        }

        Response response = client.newCall(builder.build()).execute();
        byte[] bytes = response.body().bytes();
        String result = new String(bytes, okHttp.responseCharset);
        if (response.code() == 200) {
            // 记录返回日志
            if (iotHttpProperties.getResponseLogEnabled() && log.isDebugEnabled()) {
                log.debug("Got response->{}", result);
            }
        } else {
            log.warn("接口调用失败！url:{}, StatusCode:{}, response:{}", url, response.code(), result);
            throw new IOException("接口调用失败！url:" + url + ", StatusCode:" + response.code());
        }
        return result;
    }

    @Builder
    @ToString(exclude = {"requestCharset", "responseCharset", "requestLog", "responseLog"})
    static class OkHttp {
        private final String url;
        private String method = DEFAULT_METHOD;
        private final String data;
        private String mediaType = DEFAULT_MEDIA_TYPE;
        private final Map<String, String> queryMap;
        private final Map<String, String> headerMap;
        private String requestCharset = DEFAULT_CHARSET;
        private String responseCharset = DEFAULT_CHARSET;
    }
}
