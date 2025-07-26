package com.enn.iot.dtu.common.outer.msg.util;

import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.outer.msg.dto.KafkaPropertyMessage;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdRespPoint;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class IotOutMessageUtils {

    public static List<KafkaPropertyMessage> getDataFromCmdResp(IotCmdResp cmdResp) {
        List<KafkaPropertyMessage> messages = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        Map<String, List<IotCmdRespPoint>> deviceGroup = groupPointByDevice(cmdResp.getPointList());
        deviceGroup.forEach((key, pointList) -> {
            if (pointList == null || pointList.isEmpty()) {
                return;
            }
            DtuDeviceDTO dtuDeviceDTO = IotGlobalContextUtil.MainData.getDeviceData(key);
            if (dtuDeviceDTO == null) {
                return;
            }
            KafkaPropertyMessage message = new KafkaPropertyMessage();
            message.setVersion("0.0.1");
            message.setDevId(dtuDeviceDTO.getId());
            message.setTs(timestamp);
            message.setResume( "N");
            List<KafkaPropertyMessage.Metrics> metrics = new ArrayList<>();
            pointList.forEach(point -> {
                KafkaPropertyMessage.Metrics metric = new KafkaPropertyMessage.Metrics();
                metric.setMetric(point.getPointCode());
                metric.setValue(point.getValue());
                metrics.add(metric);
            });
            message.setData(metrics);

            message.setDevType(dtuDeviceDTO.getEntityTypeCode());
            message.setDeviceName(dtuDeviceDTO.getDeviceName());
            message.setPeriod(dtuDeviceDTO.getPeriod());
            message.setSn(dtuDeviceDTO.getSn());
            message.setProductId(dtuDeviceDTO.getProductId());
            message.setTenantId(dtuDeviceDTO.getTenantId());
            message.setDeptId(dtuDeviceDTO.getDeptId());
            message.setDebug(dtuDeviceDTO.getTestFlag());
            message.setSource(dtuDeviceDTO.getEntityTypeSource());

            messages.add(message);
        });

        return messages;
    }

//    /**
//     * 消息格式转换
//     *
//     * @param cmdResp
//     * @return
//     */
//    public static IotMqttMessage getDataFromCmdResp(IotCmdResp cmdResp) {
//        long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
//        AbstractIotCmdReq cmdReq = cmdResp.getCmdReq();
//        IotMqttMessage result = new IotMqttMessage();
//        result.setGatewaySn(cmdReq.getGatewaySn());
//        result.setTimestamp(currentTime);
//
//        List<IotMqttDevice> deviceList = new LinkedList<>();
//        Map<String, List<IotCmdRespPoint>> deviceGroup = groupPointByDevice(cmdResp.getPointList());
//        deviceGroup.forEach((key, pointList) -> {
//            if (pointList == null || pointList.isEmpty()) {
//                return;
//            }
//            IotMqttDevice device = new IotMqttDevice();
//            device.setSystemAliasCode(pointList.get(0).getSystemAliasCode());
//            device.setDeviceTrdPtyCode(pointList.get(0).getDeviceTrdPtyCode());
//
//            List<IotMqttMetric> metricList = new LinkedList<>();
//            pointList.forEach(point -> {
//                IotMqttMetric metric = new IotMqttMetric();
//                metric.setPointCode(point.getPointCode());
//                metric.setTimestamp(TimeUnit.MILLISECONDS.toSeconds(point.getTimeMs()));
//                metric.setValue(point.getValue());
//                metricList.add(metric);
//            });
//            if (!metricList.isEmpty()) {
//                device.setPointList(metricList);
//                deviceList.add(device);
//            }
//        });
//        if (!deviceList.isEmpty()) {
//            result.setDeviceList(deviceList);
//        }
//        return result;
//    }

    private static Map<String, List<IotCmdRespPoint>> groupPointByDevice(List<IotCmdRespPoint> pointList) {
        if (pointList == null || pointList.isEmpty()) {
            return (Map<String, List<IotCmdRespPoint>>) Collections.EMPTY_MAP;
        }
        return pointList.stream()
                .collect(groupingBy(point -> point.getSystemAliasCode() + "_" + point.getDeviceTrdPtyCode()));
    }
}
