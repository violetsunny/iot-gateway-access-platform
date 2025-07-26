package com.enn.iot.dtu.timer.job;

import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lixiang
 * @date 2021/11/8
 **/
@Slf4j
@Component
public class CheckMainDataJob implements Runnable {

    @Override
    public void run() {
        Map<String, Channel> channelMap = IotGlobalContextUtil.Channels.getAllChannels();
        Map<String, MainDataDTO> mainDataMap = IotGlobalContextUtil.MainData.getAllMainData();
        List<String> channelWithoutMainDataList = getChannelWithoutMainDataList(channelMap, mainDataMap);
        List<String> mainDataWithoutChannelList = getMainDataWithoutChannelList(channelMap, mainDataMap);
        List<String> inactiveChannelList = getInactiveChannelList(channelMap);
        if (!channelWithoutMainDataList.isEmpty()) {
            log.warn("channelWithoutMainDataList: {}", channelWithoutMainDataList.toString());
        }
        if (!mainDataWithoutChannelList.isEmpty()) {
            log.warn("mainDataWithoutChannelList: {}", mainDataWithoutChannelList.toString());
        }
        if (!inactiveChannelList.isEmpty()) {
            log.warn("inactiveChannelList: {}", inactiveChannelList.toString());
        }
    }

    private List<String> getChannelWithoutMainDataList(Map<String, Channel> channelMap,
        Map<String, MainDataDTO> mainDataMap) {
        // 转换Map.Entry为String
        return channelMap.keySet().stream()
            // 过滤：MainData不存在，并且没有在刷新主数据的channel
            .filter(gatewaySn ->
                    // 不是正在刷新主数据
            !IotGlobalContextUtil.MainData.getRefreshing(gatewaySn)
                    // MainData不存在
                && !mainDataMap.containsKey(gatewaySn))
            // 聚合为List
            .collect(Collectors.toList());
    }

    private List<String> getMainDataWithoutChannelList(Map<String, Channel> channelMap,
        Map<String, MainDataDTO> mainDataMap) {
        // 转换Map.Entry为String
        return mainDataMap.keySet().stream()
            // 过滤：MainData存在，channel不存在的
            .filter(mainDataDTO -> !channelMap.containsKey(mainDataDTO))
            // 聚合为List
            .collect(Collectors.toList());
    }

    private List<String> getInactiveChannelList(Map<String, Channel> channelMap) {
        return channelMap.entrySet().stream()
            // 过滤：连接状态是断开的channel
            .filter((Map.Entry<String, Channel> entry) -> !entry.getValue().isActive())
            // 转换Map.Entry为String
            .map(Map.Entry::getKey)
            // 聚合为List
            .collect(Collectors.toList());
    }
}
