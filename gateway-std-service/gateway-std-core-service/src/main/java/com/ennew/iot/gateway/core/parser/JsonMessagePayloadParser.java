package com.ennew.iot.gateway.core.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class JsonMessagePayloadParser {

    public static Map<String, Object> parseExpression(List<? extends MetadataMapping> metadataMappings, Object body) {
        String payload = JSON.toJSONString(body);
        Map<String, Object> metaMap = new HashMap<>();

        metadataMappings.forEach(metadataMapping -> {
            if (metadataMapping.getPathValue() == null || "".equals(metadataMapping.getPathValue().trim()) || !JSONPath.contains(body, metadataMapping.getPathValue())) {
                return;
            }
            Object value = JSONPath.read(payload, metadataMapping.getPathValue());
            log.info("json解析器解析上报属性/事件{},值为{}", metadataMapping.getPathValue(), value);
            if (Objects.isNull(value)) {
                return;
            }
            if (metadataMapping.getIsList() == null || metadataMapping.getIsList() == 0) {
                metaMap.put(metadataMapping.getCode(), typeOf(value, metadataMapping.getDataType()));
            } else {
                metaMap.put(metadataMapping.getCode(), parseExpressions(metadataMapping.getParameters(), value));
            }

        });

        return metaMap;
    }

    public static Object typeOf(Object value, String dataType) {
        String newValue = String.valueOf(value);
        switch (dataType) {
            case "Double":
                return Double.parseDouble(newValue);
            case "Float":
                return Float.parseFloat(newValue);
            case "Int":
                return Integer.parseInt(newValue);
            case "Long":
                return Long.parseLong(newValue);
            case "BigDecimal":
                return new BigDecimal(newValue);
            case "Boolean":
                return Boolean.parseBoolean(newValue);
            case "Date":
                Long timestamp = null;
                if (newValue.length() == 19) {
                    timestamp = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseLocalDateTime(newValue).toDate().getTime();
                }
                if (newValue.length() == 10) {
                    timestamp = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDateTime(newValue).toDate().getTime();
                }
                return timestamp;
            default:
                return newValue;
        }
    }

    public static List<Map<String, Object>> parseExpressions(List<? extends MetadataMapping> metadataMappings, Object body) {
        if (!(body instanceof List)) {
            return null;
        }
        List<Map<String, Object>> metas = new ArrayList<>();
        List list = (List) body;
        for (int i = 0; i < list.size(); i++) {
            metas.add(parseExpression(metadataMappings, list.get(i)));
        }
        return metas;
    }

}