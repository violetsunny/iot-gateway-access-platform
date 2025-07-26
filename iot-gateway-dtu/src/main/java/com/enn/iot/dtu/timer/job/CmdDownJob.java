package com.enn.iot.dtu.timer.job;

import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.service.ControlCmdService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 下行指令轮询任务
 *
 * @author Mr.Jia
 * @date 2022/7/22 6:05 PM
 */
@Slf4j
@Component
public class CmdDownJob implements Runnable {

    private final ControlCmdService controlCmdService;

    public CmdDownJob(ControlCmdService controlCmdService) {
        this.controlCmdService = controlCmdService;
    }

    @Override
    public void run() {
        Map<String, Channel> channelMap = IotGlobalContextUtil.WriteGatewayChannels.getAllChannels();
        List<String> list = getWriteGatewayChannelList(channelMap);
        if (log.isInfoEnabled()) {
            log.info("[JOB] 加载写指令作业，控制类DTU在线数量：{}，在线DTU清单：{}", list.size(), list);
        }
        if (!list.isEmpty()) {
            list.forEach(controlCmdService::loadDtuControlCommand);
        }
    }

    private List<String> getWriteGatewayChannelList(Map<String, Channel> channelMap) {
        return channelMap.entrySet().stream()
            // 过滤：连接状态是打开的channel
            .filter((Map.Entry<String, Channel> entry) -> entry.getValue().isActive())
            // 转换Map.Entry为String
            .map(Map.Entry::getKey)
            // 聚合为List
            .collect(Collectors.toList());
    }
}
