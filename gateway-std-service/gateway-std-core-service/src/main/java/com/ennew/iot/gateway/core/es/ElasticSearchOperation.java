/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.es;

import com.alibaba.fastjson.JSON;
import com.ennew.iot.gateway.common.enums.EsDataTypeEnum;
import com.ennew.iot.gateway.common.utils.JsonUtils;
import com.ennew.iot.gateway.core.es.index.EsDataEvent;
import com.ennew.iot.gateway.core.es.index.EsSourceData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import top.kdla.framework.common.help.DateHelp;
import top.kdla.framework.common.help.MultiThreadInvokeHelp;
import top.kdla.framework.common.help.ThreadPoolHelp;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 7.17版本
 *
 * @author kanglele
 * @version $Id: ElasticSearchService, v 0.1 2023/2/20 11:36 kanglele Exp $
 */
@Slf4j
@Service
public class ElasticSearchOperation {

    //    @Resource
//    private ElasticsearchTemplate elasticsearchOperations;
//    @Resource
//    private ThreadPoolHelp threadPoolHelp;
    @Autowired
    protected BulkProcessor bulkProcessor;

    public void saveEsLog(EsDataEvent esDataEvent) {
        IndexRequest request = new IndexRequest(getIndexName(esDataEvent));
        request.id(esDataEvent.getSn() + "_" + esDataEvent.getUuid());
        request.source(JsonUtils.writeValueAsString(esDataEvent), XContentType.JSON);
        request.type("_doc");
        bulkProcessor.add(request);
    }

    private String getIndexName(EsDataEvent esDataEvent) {
        return "custom_" + EsDataTypeEnum.getEsIndexType(esDataEvent.getType()) + "_" + DateHelp.format(new Date(), "yyyy-MM-dd-HH");
    }


    public void saveOriginalDeviceReport(String content) {
        LocalDateTime now = LocalDateTime.now();
        String index = String.format("custom_metric_%04d-%02d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour());
        IndexRequest request = new IndexRequest(index);
        request.id(UUID.randomUUID().toString());
        request.source(content, XContentType.JSON);
        bulkProcessor.add(request);
    }

}
