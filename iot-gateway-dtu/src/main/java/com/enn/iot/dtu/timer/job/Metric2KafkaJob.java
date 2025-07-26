package com.enn.iot.dtu.timer.job;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.metric.dto.IotMetricMessage;
import com.enn.iot.dtu.integration.kafka.IotKafkaClient;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description DTU主数据变化更新定时作业
 * @Author nixiaolin
 * @Date 2021/11/2 14:11
 */
@Slf4j
@Component
public class Metric2KafkaJob implements Runnable {

    @Autowired
    private IotKafkaClient iotKafkaClient;

    @Override
    public void run() {
        try {
            final long time = System.currentTimeMillis();
            final Map<String, MainDataDTO> allDtu = IotGlobalContextUtil.MainData.getAllMainData();
            final List<IotMetricMessage> metricList = new ArrayList<>(allDtu.size());
            allDtu.forEach((gatewaySn, mainData) -> {
                if (gatewaySn == null || mainData == null) {
                    log.warn("[JOB] 发送指标，网关档案信息为空! gatewaySn: {}, mainData: {}", gatewaySn, mainData);
                    return;
                }
                Channel channel = IotGlobalContextUtil.Channels.getChannel(gatewaySn);
                if (channel == null) {
                    if (log.isWarnEnabled()) {
                        log.warn(IotChannelContextUtil.Log.context(gatewaySn) + "[JOB] 发送指标，网关连接信息为空! channel为null");
                    }
                    return;
                }
                if (channel.isActive()) {
                    IotMetricMessage metricMsg = new IotMetricMessage();
                    metricMsg.setTime(time);
                    metricMsg.setOnlineStatus(true);
                    metricMsg.setConnectedTime(
                        IotChannelContextUtil.Connection.getConnectedTimeMs(channel.pipeline().lastContext()));
                    IotMetricMessage.IotMetricTags tags = metricMsg.getTags();
                    tags.setAppId("iot-gateway-dtu");
                    tags.setGatewayId(gatewaySn);
                    tags.setSystemCode(mainData.getStationId());
                    metricList.add(metricMsg);
                }
            });
            sendMetricsWithGroup(metricList);
            if (log.isInfoEnabled()) {
                List<String> gateways = metricList.stream().map(metricMsg -> metricMsg.getTags().getGatewayId())
                    .collect(Collectors.toList());
                log.info("[JOB] 发送指标，在线DTU数量: {}, 在线DTU清单: {}", metricList.size(), gateways);
            }
        } catch (Exception e) {
            log.error("[JOB] 发送指标异常！", e);
        }
    }

    private void sendMetricsWithGroup(List<IotMetricMessage> metricList) {
        List<List<IotMetricMessage>> groupList = splitList(metricList, 500);
        groupList.forEach(oneGroup -> iotKafkaClient.sendMetricData(oneGroup));
    }

    private <T> List<List<T>> splitList(List<T> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }
}