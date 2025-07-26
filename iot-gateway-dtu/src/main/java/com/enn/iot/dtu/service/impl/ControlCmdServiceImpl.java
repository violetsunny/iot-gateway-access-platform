package com.enn.iot.dtu.service.impl;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.msg.IotCmdRespond;
import com.enn.iot.dtu.common.msg.enums.IotRespondCmdStatus;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.constant.Constants;
import com.enn.iot.dtu.integration.kafka.IotKafkaClient;
import com.enn.iot.dtu.integration.open.IotOpenClient;
import com.enn.iot.dtu.integration.open.ResControlCmdDTO;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import com.enn.iot.dtu.protocol.api.dto.DtuCmdDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.CimPointDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import com.enn.iot.dtu.protocol.modbus.dto.IotReadCmdReq4Modbus;
import com.enn.iot.dtu.protocol.modbus.dto.IotWriteCmdReq4Modbus;
import com.enn.iot.dtu.service.CmdExecuteService;
import com.enn.iot.dtu.service.ControlCmdService;
import com.enn.iot.dtu.service.MainDataService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * 下行指令实现类
 *
 * @author Mr.Jia
 * @date 2022/7/23 9:44 AM
 */
@Slf4j
@Service
@AllArgsConstructor
public class ControlCmdServiceImpl implements ControlCmdService {
    private final IotOpenClient iotOpenClient;
    private final CmdExecuteService cmdExecuteService;
    private final MainDataService mainDataService;
    private final IotKafkaClient iotKafkaClient;



    /**
     * 加载下行指令
     *
     * @param gatewaySn
     *            网关标识
     * @author Mr.Jia
     * @date 2022/7/23 9:43 AM
     */
    @Override
    public synchronized void loadDtuControlCommand(String gatewaySn) {
        if (insertCmdToWriteQueueByGatewaySn(gatewaySn)) {
            return;
        }
        // 调用尝试执行下一条指令执行指令
        cmdExecuteService.executeNextCmdIf(gatewaySn);
    }

    /**
     * 查询一条控制指令并添加到高优先级指令队列里面去
     *
     * @param gatewaySn
     *            网关标识
     * @return java.lang.Boolean
     * @author Mr.Jia
     * @date 2022/9/28 11:07 PM
     */
    @Override
    public synchronized Boolean insertCmdToWriteQueueByGatewaySn(String gatewaySn) {
        // 记录接受指令的时间
        long timeMillis = System.currentTimeMillis();
        // 获取 ChannelHandlerContext
        ChannelHandlerContext ctx = getChannelHandlerContext(gatewaySn);
        if (ctx == null) {
            return true;
        }
        // 获取指令信息
        DtuCmdDTO cmdDTO = getDtuCmdDTO(gatewaySn);
        if (cmdDTO == null) {
            return true;
        }
        // 创建一个指令信息对象
        ControlCmdDTO controlCmd = new ControlCmdDTO();
        // 错误信息收集到 map
        Map<String, String> errMap = controlCmd.validate(cmdDTO);
        // 获取当前云网关的主数据信息
        MainDataDTO mainDataDTO = IotChannelContextUtil.MainData.getMainData(ctx);

        if (mainDataDTO == null) {
            errMap.put("mainData", "未找到对应的档案数据");
        }

        if (mainDataDTO != null) {
            List<DtuDeviceDTO> deviceList = mainDataDTO.getDeviceList();
            if (deviceList == null || deviceList.isEmpty()) {
                errMap.put("deviceList", "DTU下面绑定的所有子设备不存在");
            }

            if (!CollectionUtils.isEmpty(deviceList)) {
                for (DtuDeviceDTO deviceDTO : deviceList) {
                    if (deviceDTO.getPointInfo() == null || deviceDTO.getPointInfo().isEmpty()) {
                        continue;
                    }
                    List<CimPointDTO> ptList = deviceDTO.getPointInfo().stream()
                        .filter((CimPointDTO cimPointInfo) -> cimPointInfo.getMeasureCat().equals(cmdDTO.getMetric()))
                        .collect(Collectors.toList());
                    if (ptList.isEmpty()) {
                        continue;
                    }
                    DtuDeviceDTO dtuDevice = new DtuDeviceDTO();
                    dtuDevice.setCommcAddr(deviceDTO.getCommcAddr());
                    dtuDevice.setCommcPrcl(deviceDTO.getCommcPrcl());
                    dtuDevice.setId(deviceDTO.getId());
                    dtuDevice.setStationId(deviceDTO.getStationId());
                    dtuDevice.setTrdPtyCode(deviceDTO.getTrdPtyCode());
                    dtuDevice.setFramingLength(deviceDTO.getFramingLength());
                    dtuDevice.setPointInfo(ptList);
                    controlCmd.setDeviceDTO(dtuDevice);
                    controlCmd.setCommcAddr(deviceDTO.getCommcAddr());
                    controlCmd.setCommcPrcl(deviceDTO.getCommcPrcl());
                    break;
                }

                if (controlCmd.getDeviceDTO() == null) {
                    errMap.put("deviceDTO", "档案中未找到指令对应的测点信息");
                }
            }

        }

        controlCmd.setCmdDTO(cmdDTO.clone());
        controlCmd.setGatewaySn(gatewaySn);
        // 接受指令的时间戳
        controlCmd.setReceiveTs(timeMillis);

        // 更新全局上下文中的DTU下发指令的档案信息
        IotChannelContextUtil.Cmd.setCurrentWriteCmdMainData(ctx, controlCmd);

        // 发送错误信息至 kafka
        if (sendErrorInfoToKafka(errMap, controlCmd, ctx)) {
            return true;
        }
        List<AbstractIotCmdReq> cmdReqList = mainDataService.generateWriteCmdListByProtocol(controlCmd);
        // 新增写指令到高优先级队列(同时增加写和读指令到队列里面)
        appendHighQueue(gatewaySn, cmdReqList, ctx);
        return false;
    }

    /**
     * 新增写指令到高优先级队列(同时增加写和读指令到队列里面)
     *
     * @param gatewaySn
     *            网关标识
     * @param cmdReqList
     *            指令
     * @param ctx
     * @author Mr.Jia
     * @date 2022/9/29 9:19 PM
     */
    public synchronized void appendHighQueue(String gatewaySn, List<AbstractIotCmdReq> cmdReqList,
        ChannelHandlerContext ctx) {
        if (cmdReqList.size() > 0) {
            Queue<AbstractIotCmdReq> queue = IotGlobalContextUtil.WriteCmd.getQueue(gatewaySn);
            AbstractIotCmdReq abstractIotCmdReq = cmdReqList.get(0);
            // 如果为写指令,生成写指令对应的读指令,追加到高优先级队列中
            if (!abstractIotCmdReq.getReadonly()) {
                IotWriteCmdReq4Modbus writeCmdReq4Modbus = (IotWriteCmdReq4Modbus)abstractIotCmdReq;
                // 新增写指令到高优先级队列
                queue.add(writeCmdReq4Modbus);
                IotReadCmdReq4Modbus readCmdReq4Modbus = writeCmdReq4Modbus.getIotReadCmdReq4Modbus();
                if (readCmdReq4Modbus != null) {
                    // 新增写指令对应的读指令到高优先级队列
                    queue.add(readCmdReq4Modbus);
                }
            }
        }
    }

    /**
     * 发送错误信息至 kafka
     *
     * @param errMap
     *            错误信息容器
     * @param controlCmd
     *            指令信息
     * @param ctx
     *            上下文
     * @return boolean
     * @author Mr.Jia
     * @date 2022/8/1 3:11 PM
     */
    private boolean sendErrorInfoToKafka(Map<String, String> errMap, ControlCmdDTO controlCmd,
        ChannelHandlerContext ctx) {
        if (errMap.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String property : errMap.keySet()) {
                String str = String.format("%s %s", property, errMap.get(property));
                builder.append(str).append(",");
            }
            if (builder.length() > 0 && builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            long ts = System.currentTimeMillis();
            IotCmdRespond cmdRespond = new IotCmdRespond();
            cmdRespond.setCmdresult(IotRespondCmdStatus.FAIL.getValue());
            cmdRespond.setGatewaySerialNum(controlCmd.getGatewaySn());
            cmdRespond.setMetric(controlCmd.getCmdDTO().getMetric());
            cmdRespond.setRecvframe("");
            cmdRespond.setSendframe("");
            cmdRespond.setRecvts(ts);
            cmdRespond.setRespondmessage(builder.toString());
            ControlCmdDTO mainData = IotChannelContextUtil.Cmd.getCurrentWriteCmdMainData(ctx);
            if (mainData != null) {
                cmdRespond.setSendts(mainData.getReceiveTs());
                cmdRespond.setTs(mainData.getCmdDTO().getTs());

            }
            cmdRespond.setSeq(controlCmd.getCmdDTO().getSeq());
            cmdRespond.setStationId(controlCmd.getDeviceDTO().getStationId());
            cmdRespond.setTrdPtyCode(controlCmd.getDeviceDTO().getTrdPtyCode());
            cmdRespond.setValue(controlCmd.getCmdDTO().getValue().toString());
            iotKafkaClient.sendCmdRespond(cmdRespond);
            if (log.isWarnEnabled()) {
                log.warn(Log.context(ctx) + "[config]事件：下发写指令校验失败事件！详细信息：{}", JsonUtils.writeValueAsString(cmdRespond));
            }
            return true;
        }
        return false;
    }

    /**
     * 调用iot-service-open接口/dtu/cmd/query获取dtu控制下行指令信息
     *
     * @param gatewaySn
     *            网关标识
     * @return com.enn.iot.dtu.protocol.api.cmddata.dto.DtuCmdDTO
     * @author Mr.Jia
     * @date 2022/8/1 2:41 PM
     */
    @Nullable
    private DtuCmdDTO getDtuCmdDTO(String gatewaySn) {
        //TODO:获取
        ResControlCmdDTO resControlCmdDTO = null;
        try {
            resControlCmdDTO = iotOpenClient.loadControlCommand(gatewaySn);
        } catch (Exception ex) {
            log.warn("[loadCmdInfo] 调用iot-service-open接口 /dtu/cmd/query 发生未知异常，获取dtu控制下行指令失败，gatewaySn={}", gatewaySn);
            return null;
        }

        if (resControlCmdDTO == null || resControlCmdDTO.getData() == null
            || StringUtils.isEmpty(resControlCmdDTO.getCode())
            || !resControlCmdDTO.getCode().equals(Constants.IOT_SERVICE_SUCCESS_CODE)) {
            if (resControlCmdDTO != null
                && resControlCmdDTO.getCode().equals(Constants.IOT_SERVICE_SUCCESS_DATA_NULL_CODE)) {
                if (log.isDebugEnabled()) {
                    log.debug("[loadCmdInfo] 获取下行指令信息数据为空，不执行下发指令，网关标识：{}，指令信息：{}", gatewaySn, resControlCmdDTO);
                }
                return null;
            }
            log.warn("[loadCmdInfo] 根据网关标识获取下行指令信息接口调用失败，返回结果不正确，入参：{}，出参：{}", gatewaySn, resControlCmdDTO);
            return null;
        }
        return resControlCmdDTO.getData();
    }

    /**
     * 检查当前云网关是否具备下发写指令的条件以及获取上下文ChannelHandlerContext
     *
     * @param gatewaySn
     *            网关标识
     * @return io.netty.channel.ChannelHandlerContext
     * @author Mr.Jia
     * @date 2022/8/1 2:40 PM
     */
    @Nullable
    private ChannelHandlerContext getChannelHandlerContext(String gatewaySn) {
        Channel channel = IotGlobalContextUtil.Channels.getChannel(gatewaySn);
        if (channel == null) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(gatewaySn) + "[loadCmdInfo] 执行写指令失败，DTU没有连接或已断开连接！");
            }
            return null;
        }
        // 下发指令后需要调用此方法保存具有下发控制指令的网关到缓存
        IotGlobalContextUtil.WriteGatewayChannels.addChannel(gatewaySn, channel);
        // 判断高优先级队列是否为空
        ChannelHandlerContext ctx = channel.pipeline().lastContext();
        int writeCmdSize = IotChannelContextUtil.WriteCmd.getQueueSize(ctx);
        if (writeCmdSize > 0) {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(gatewaySn) + "[loadCmdInfo] 高优先级指令队列当前不为空，不执行控制指令操作！当前的高优先级指令队列，共{}条",
                    writeCmdSize);
                return null;
            }
        }

        if (StringUtils.isEmpty(gatewaySn)) {
            log.warn("[config] 网关标识为空，终止加载控制下行指令操作");
            return null;
        }
        return ctx;
    }
}
