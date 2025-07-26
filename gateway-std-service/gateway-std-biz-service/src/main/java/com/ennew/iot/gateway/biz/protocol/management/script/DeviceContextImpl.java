package com.ennew.iot.gateway.biz.protocol.management.script;

import cn.enncloud.iot.gateway.context.DeviceContext;
import cn.enncloud.iot.gateway.entity.Device;
import cn.enncloud.iot.gateway.entity.Product;
import cn.enncloud.iot.gateway.entity.tsl.ProductTsl;
import cn.enncloud.iot.gateway.message.Message;
import cn.enncloud.iot.gateway.message.Metric;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.core.bo.TrdPlatformMeasureRefBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.core.repository.TrdPlatformMeasureRefRepository;
import com.ennew.iot.gateway.core.repository.TrdPlatformModelRefRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class DeviceContextImpl implements DeviceContext {

    @Resource
    private TrdPlatformModelRefRepository trdPlatformModelRefRepository;

    @Resource
    private TrdPlatformMeasureRefRepository trdPlatformMeasureRefRepository;

    @Override
    public ProductTsl getTslByDeviceId(String s) {
        return null;
    }

    @Override
    public ProductTsl getTslByProductId(String s) {
        return null;
    }

    @Override
    public ProductTsl getTslByCode(String s) {
        return null;
    }

    @Override
    public String getDeviceIdBySn(String s, String s1) {
        return "run-test-DeviceId";
    }

    @Override
    public boolean authDevice(String s, String s1) {
        return false;
    }

    @Override
    public Metric getLastDevcieMetric(String s, String s1) {
        return null;
    }

    @Override
    public void storeMessage(Message message) {

    }

    @Override
    public List<Device> getSnByProductId(String s) {
        ArrayList<Device> objects = new ArrayList<>();
        Device device = new Device();
        device.setSn("run-test-Sn");
        objects.add(device);
        return objects;
    }

    @Override
    public List<String> getDeviceIdByProductId(String s) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("run-test-DeviceId");
        return objects;
    }

    @Override
    public Product getProductByDeviceId(String s) {
        return null;
    }

    @Override
    public String getSnByDeviceId(String s) {
        return "run-test-Sn";
    }

    @Override
    public String getProductProtocolBySn(String s) {
        return null;
    }

    @Override
    public List<String> getImeiByProductId(String s) {
        return null;
    }

    @Override
    public String getDeviceIdByImei(String s) {
        return null;
    }

    @Override
    public void updateImei(List<Device> list) {
    }

    @Override
    public String registerDevice(Device device) {
        return null;
    }


    @Override
    public void putDeviceProtocol(String s, String s1) {
    }

    public String modelRefMap(String code, String productId, String time, String metrics) {
        TrdPlatformModelRefBo modelRefBo = trdPlatformModelRefRepository.queryByCode(code, productId);
        if (modelRefBo != null) {
            List<TrdPlatformMeasureRefBo> refBos = trdPlatformMeasureRefRepository.queryById(modelRefBo.getId());
            if (CollectionUtils.isNotEmpty(refBos)) {
                String jsonString = modelMapping(metrics, time, refBos, true);

                return jsonString;
            }

        }
        return metrics;
    }

    private String modelMapping(String data, String time, List<TrdPlatformMeasureRefBo> trdPlatformMeasureRefList, boolean isStayOrigin) {

        Map<String, String> measureMap = trdPlatformMeasureRefList.stream().collect(Collectors.toMap(TrdPlatformMeasureRefBo::getPlatformMeasureCode, TrdPlatformMeasureRefBo::getEnnMeasureCode));

        JSONArray dataArray = null;
        try {
            dataArray = JSONObject.parseArray(data);
        } catch (Exception e) {
            throw new RuntimeException("modelRefMap(String code, String productId, String time, String metrics) input error, metrics must be jsonArrayStr type");
        }

        long dataTime;
        try {
            dataTime = Long.parseLong(time);
        } catch (NumberFormatException e) {
            throw new RuntimeException("modelRefMap(String code, String productId, String time, String metrics) input error, time must be long type");
        }

        List<Metric> resultMetrics = new ArrayList<>();
        long finalDataTime = dataTime;
        dataArray.forEach(metric -> {

            Metric metric1 = null;
            try {
                metric1 = JSONObject.parseObject(metric.toString(), Metric.class);

                Optional.ofNullable(metric1.getCode()).orElseThrow((Supplier<Throwable>) () -> new Throwable("code must not be null"));
                Optional.ofNullable(metric1.getValue()).orElseThrow((Supplier<Throwable>) () -> new Throwable("value must not be null"));

                if (measureMap.containsKey(metric1.getCode())) {

                    String ennKey = measureMap.get(metric1.getCode());
                    metric1.setCode(ennKey);
                    metric1.setTs(finalDataTime);
                    resultMetrics.add(metric1);
                } else {
                    if (isStayOrigin) {
                        metric1.setTs(finalDataTime);
                        resultMetrics.add(metric1);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("metrics element process error, metric:" + metric.toString());
            } catch (Throwable e) {
                throw new RuntimeException("metrics element process error, cause:" + e.getMessage());
            }
        });

        String jsonString = JSONObject.toJSONString(resultMetrics);
        return jsonString;
    }

    @Override
    public Map<String, String> modelRef(String s, String s1, Object o) {
        return Collections.emptyMap();
    }

    @Override
    public String modelRefMetric(String s, Map<String, String> map) {
        return s;
    }

    @Override
    public boolean validSnBelongProduct(String s, String s1) {
        return false;
    }
}
