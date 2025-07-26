package com.enn.iot.dtu.service.impl;

import com.enn.iot.dtu.common.context.IotChannelContextUtil;
import com.enn.iot.dtu.common.context.IotGlobalContextUtil;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.bcs.IotBcsClient;
import com.enn.iot.dtu.integration.bcs.dto.ResDevicePointDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResLastTimeUpdateDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResListDeviceDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResListPointInfoDTO;
import com.enn.iot.dtu.integration.cit.IotCitClient;
import com.enn.iot.dtu.integration.cit.dto.ResGatewaySerialNumDTO;
import com.enn.iot.dtu.integration.constant.Constants;
import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.api.maindata.dto.CimPointDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuPointInfoDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import com.enn.iot.dtu.protocol.factory.IotProtocolCodecFactory;
import com.enn.iot.dtu.service.CmdExecuteService;
import com.enn.iot.dtu.service.MainDataService;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Auth;
import com.enn.iot.dtu.common.context.IotChannelContextUtil.Log;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
//@AllArgsConstructor
public class MainDataServiceImpl implements MainDataService {

    private final CmdExecuteService cmdExecService;
    private final IotBcsClient bcsClient;
    private final IotCitClient citClient;

    public MainDataServiceImpl(CmdExecuteService cmdExecService, IotBcsClient bcsClient, IotCitClient citClient) {
        this.cmdExecService = cmdExecService;
        this.bcsClient = bcsClient;
        this.citClient = citClient;
    }

    @Value("${modbus-dtu.pointTable.refreshInterval:60}")
    private Integer refreshInterval;

    /**
     * iot-service-cit 查询网关是否存在
     *
     * @param registerPackage
     * @return
     */
    @Override
    public String getGatewaySnByRegisterPackage(String registerPackage) {
        if (StringUtils.isEmpty(registerPackage)) {
            log.error("[config] 根据注册包获取网关标识入参为空");
            return null;
        }

        ResGatewaySerialNumDTO dto;
        try {
            dto = citClient.getGatewaySnByRegisterPackage(registerPackage);
        } catch (Exception ex) {
            log.error("[config] 根据注册包获取网关标识接口异常, 注册包:" + registerPackage, ex);
            return null;
        }

        if (// 返回结果为null
                null == dto
                        // 状态码为空
                        || StringUtils.isEmpty(dto.getCode())
                        // 状态码不是success
                        || !dto.getCode().equals(Constants.IOT_SERVICE_SUCCESS_CODE)
                        // 数据为空
                        || null == dto.getData() || StringUtils.isEmpty(dto.getData().getSerialNum())) {
            log.warn("[config] 根据注册包获取网关标识失败，入参：{}，出参：{}", registerPackage, dto);
            return null;
        }

        return dto.getData().getSerialNum();
    }

    /**
     * iot-service-bcs 查询档案配置更新时间
     *
     * @param gatewaySn
     * @return
     */
    private Long queryMainDataUpdateTimeByGatewaySn(String gatewaySn) {
        if (StringUtils.isEmpty(gatewaySn)) {
            log.error("[config] 调用接口 queryMainDataUpdateTimeByGatewaySn 函数入参为空");
            return null;
        }

        ResLastTimeUpdateDTO result;
        try {
            result = bcsClient.queryMainDataUpdateTimeByGatewaySn(gatewaySn);
        } catch (Exception ex) {
            log.error(Log.context(gatewaySn) + "[config] 调用接口 queryMainDataUpdateTimeByGatewaySn 异常", ex);
            return null;
        }

        if (// 返回结果为null
                null == result
                        // 状态码为空
                        || StringUtils.isEmpty(result.getCode())
                        // 状态码不是success
                        || !result.getCode().equals(Constants.IOT_SERVICE_SUCCESS_CODE)
                        // 数据为空
                        || null == result.getData()) {
            log.warn(Log.context(gatewaySn) + "[config] 获取档案更新时间失败，出参：{}",
                    result == null ? null : JsonUtils.writeValueAsString(result));
            return null;
        }
        return result.getData();
    }

    private List<DtuPointInfoDTO> queryPointListByGatewaySn(String gatewaySn) {
        if (StringUtils.isEmpty(gatewaySn)) {
            log.error("[config] 调用 queryPointListByGatewaySn 函数入参为空");
            return null;
        }
        ResListPointInfoDTO result;
        try {
            result = bcsClient.queryPointListByGatewaySn(gatewaySn);
        } catch (Exception ex) {
            log.error(Log.context(gatewaySn) + "[config] 调用 queryPointListByGatewaySn 接口异常", ex);
            return null;
        }

        if (null == result
                // 状态码为空
                || StringUtils.isEmpty(result.getCode())
                // 状态码不是success
                || !result.getCode().equals(Constants.IOT_SERVICE_SUCCESS_CODE)
                // 数据为空
                || null == result.getData()) {
            log.warn(Log.context(gatewaySn) + "[config] 根据网关标识获取测点信息失败！出参：{}",
                    result == null ? null : JsonUtils.writeValueAsString(result));
            return null;
        }
        return result.getData();
    }

    /**
     * iot-service-bcs 查询档案配置信息
     *
     * @param gatewaySn
     * @return
     */
    private MainDataDTO queryMainDataByGatewaySn(String gatewaySn) {
        if (StringUtils.isEmpty(gatewaySn)) {
            log.error("[config] 调用 queryMainDataByGatewaySn 函数入参为空");
            return null;
        }
        List<DtuPointInfoDTO> list = queryPointListByGatewaySn(gatewaySn);
        if (list == null || list.isEmpty()) {
            log.warn(Log.context(gatewaySn) + "[config] DTU下面绑定的所有子设备下的测点不存在，为空");
            return null;
        }
        List<DtuPointInfoDTO> filterLst = list.stream()
                .filter((DtuPointInfoDTO dto) -> !gatewaySn.equals(dto.getGatewaySerialNum())).collect(Collectors.toList());
        if (!filterLst.isEmpty()) {
            log.warn(Log.context(gatewaySn) + "[config] 收到的测点数据中存在多个网关标识, 对此次获取的主数据不做更新处理，不同的网关标识列表: {}", filterLst);
            return null;
        }
        MainDataDTO mainData = processPointInfo(gatewaySn, list);
        if (mainData == null) {
            return null;
        }
        Long updateTime = queryMainDataUpdateTimeByGatewaySn(gatewaySn);
        mainData.setUpdateTime(updateTime);
        return mainData;
    }

    //

    /**
     * 根据网关id获取所有点表信息
     *
     * @param gatewaySn
     * @return 使用新接口组合实现
     */
    private MainDataDTO queryPointListByGatewaySnNew(String gatewaySn) {
        if (StringUtils.isEmpty(gatewaySn)) {
            log.error("[config] 调用 queryPointListByGatewaySn 函数入参为空");
            return null;
        }
        List<ResListDeviceDTO.DeviceInfoDTO> result;
        try {
            result = bcsClient.queryDeviceListByGatewaySn(gatewaySn);
        } catch (Exception ex) {
            log.error(Log.context(gatewaySn) + "[config] 调用 queryDeviceListByGatewaySn 接口异常", ex);
            return null;
        }

        if (CollectionUtils.isEmpty(result)) {
            log.warn(Log.context(gatewaySn) + "[config] 调用 queryDeviceListByGatewaySn 接口数据为空");
            return null;
        }
        MainDataDTO mainData = new MainDataDTO();
        mainData.setGatewaySn(gatewaySn);
        mainData.setStationId(gatewaySn);
        mainData.setUpdateTime(System.currentTimeMillis());
        List<DtuDeviceDTO> deviceList = new ArrayList<>();
        for (ResListDeviceDTO.DeviceInfoDTO deviceInfoDTO : result) {
            ResDevicePointDTO resDevicePointDTO = null;
            try {
                resDevicePointDTO = bcsClient.queryPointListByDeviceId(deviceInfoDTO.getId());
            } catch (Exception ex) {
                log.error(Log.context(gatewaySn) + "[config] 调用 queryPointListByDeviceId 接口异常,deviceInfo:{}", JsonUtils.writeValueAsString(deviceInfoDTO), ex);
            }
            if (Objects.isNull(resDevicePointDTO)) {
                log.warn(Log.context(gatewaySn) + "[config] 调用 queryPointListByDeviceId 接口数据为空,deviceInfo:{}", JsonUtils.writeValueAsString(deviceInfoDTO));
                continue;
            }

            List<ResDevicePointDTO.PointInfoDTO> pointInfoDTOS = resDevicePointDTO.getData();
            if (!CollectionUtils.isEmpty(pointInfoDTOS)) {
                DtuDeviceDTO deviceDTO = processPointInfo(deviceInfoDTO, pointInfoDTOS, gatewaySn);
                if (Objects.nonNull(deviceDTO)) {
                    deviceList.add(deviceDTO);
                }
            }
        }
        if (CollectionUtils.isEmpty(deviceList)) {
            return null;
        }
        mainData.setDeviceList(deviceList);
        return mainData;

    }

    private DtuDeviceDTO processPointInfo(ResListDeviceDTO.DeviceInfoDTO deviceInfoDTO, List<ResDevicePointDTO.PointInfoDTO> pointInfoDTOS, String gatewaySn) {
        DtuDeviceDTO deviceDTO = new DtuDeviceDTO();
        deviceDTO.setCommcAddr(deviceInfoDTO.getSlaveAddress());
        //默认固定值
        deviceDTO.setCommcPrcl(ProtocolTypeEnum.MODBUS_RTU.getValue());
        deviceDTO.setId(deviceInfoDTO.getId());
        deviceDTO.setTrdPtyCode(deviceInfoDTO.getThirdCode());
        deviceDTO.setStationId(gatewaySn);
        deviceDTO.setFramingLength(10);
        deviceDTO.setDelayDefensive(1);

        deviceDTO.setDeviceName(deviceInfoDTO.getName());
        deviceDTO.setPeriod(deviceInfoDTO.getPeriod());
        deviceDTO.setSn(deviceInfoDTO.getSn());
        deviceDTO.setProductId(deviceInfoDTO.getProductId());
        deviceDTO.setTenantId(deviceInfoDTO.getTenantId());
        deviceDTO.setDeptId(deviceInfoDTO.getDeptId());
        deviceDTO.setTestFlag(deviceInfoDTO.getTestFlag());
        deviceDTO.setEntityTypeCode(deviceInfoDTO.getEntityTypeCode());
        deviceDTO.setEntityTypeSource(deviceInfoDTO.getEntityTypeSource());

        List<CimPointDTO> pointDTOS = new ArrayList<>();
        pointInfoDTOS.forEach(pointInfoDTO -> {
            CimPointDTO cimPointDTO = new CimPointDTO();
            String address = pointInfoDTO.getAddress();
            String functionCode = null;
            String addr = null;
            try {
                String[] split = address.split("!");
                String slaveAddr = split[0];
                String blockAddr = split[1];
                char c = blockAddr.charAt(0);
                functionCode = transFunctionCode(c);
                int addrCode = Integer.parseInt(blockAddr.substring(1));
                addr= String.valueOf(addrCode);
            } catch (Exception e) {
                log.error("地址解析异常，不支持此格式类型，address：{},error:{}", address,e.getMessage());
                return;
            }

            cimPointDTO.setModBus(addr);
            cimPointDTO.setFunctionCode(functionCode);

            String parsingMode = pointInfoDTO.getParsingMode();
            int bitOffset = pointInfoDTO.getBitOffset();

            // 解析类型转换
            String modBusDataType = transferDataType(parsingMode, bitOffset);
            if (StringUtils.isEmpty(modBusDataType)) {
                return;
            }
            cimPointDTO.setModBusDataType(modBusDataType);
            cimPointDTO.setByteOrder(pointInfoDTO.getByteOrder());
            cimPointDTO.setMeasureCat(pointInfoDTO.getMeasureCode());
            cimPointDTO.setName(pointInfoDTO.getMeasureName());
            cimPointDTO.setUnit(pointInfoDTO.getMeasureUnit());
            cimPointDTO.setReadWriteRoleName(pointInfoDTO.getReadWriteRight());

            pointDTOS.add(cimPointDTO);
        });
        if (!CollectionUtils.isEmpty(pointDTOS)) {
            deviceDTO.setPointInfo(pointDTOS);
            return deviceDTO;
        }
        return null;
    }


    @Override
    public boolean refreshMainDataIf(ChannelHandlerContext ctx) throws Exception {
        return refreshMainDataIf(Auth.getGatewaySn(ctx), ctx);
    }

    /**
     * 检查并更新DTU的采集配置信息
     *
     * @param gatewaySn
     */
    @Override
    public boolean refreshMainDataIf(String gatewaySn) {
        return refreshMainDataIf(gatewaySn, null);
    }

    /**
     * 检查并更新DTU的采集配置信息
     */
    boolean refreshMainDataIf(String gatewaySn, ChannelHandlerContext ctx) {
        // 设置刷新状态
        IotGlobalContextUtil.MainData.setRefreshing(gatewaySn, true);

        // 查询本地配置时间
        Long localUpdateTime = IotGlobalContextUtil.MainData.getUpdateTime(gatewaySn);
        boolean needRefresh = false;
        try {
            // 查询远端配置时间
            Long remoteUpdateTime = System.currentTimeMillis();
            // 超过60s更新配置
            needRefresh = localUpdateTime == null || (remoteUpdateTime - localUpdateTime > refreshInterval * 1000);
            if (needRefresh) {
                MainDataDTO mainData = queryPointListByGatewaySnNew(gatewaySn);
                if (mainData == null) {
                    log.warn(Log.context(ctx, gatewaySn) + "[config] 查询采集配置为空！");
                    mainData = MainDataDTO.newInstance(gatewaySn);
                }
                mainData.setUpdateTime(remoteUpdateTime);
                // 更新全局上下文中的DTU档案信息
                if (!CollectionUtils.isEmpty(mainData.getDeviceList())) {
                    Map<String, List<DtuDeviceDTO>> dtuDeviceDTOMap = mainData.getDeviceList().stream().collect(Collectors.groupingBy(device -> device.getStationId() + "_" + device.getTrdPtyCode()));
                    IotGlobalContextUtil.MainData.setDeviceDataMap(dtuDeviceDTOMap);
                    IotGlobalContextUtil.MainData.setMainData(gatewaySn, mainData);
                    // 生成读指令列表
                    List<AbstractIotCmdReq> readCmdList = generateReadCmdListByMainData(mainData);
                    // 更新读指令队列
                    IotGlobalContextUtil.ReadCmd.setList(gatewaySn, readCmdList);
                    if (log.isDebugEnabled()) {
                        log.debug(Log.context(ctx, gatewaySn) + "[config] 加载采集配置成功！读指令列表: {}",
                                JsonUtils.writeValueAsString(readCmdList));
                    }
                    // 如果当前没有指令在执行，则执行首个指令。
                    cmdExecService.executeNextCmdIf(gatewaySn);
                }
            }
        } catch (Exception e) {
            log.error(Log.context(ctx, gatewaySn) + "刷新配置异常！", e);
        }
        // 更新刷新状态
        IotGlobalContextUtil.MainData.setRefreshing(gatewaySn, false);
        return needRefresh;
    }

    /**
     * 生成DTU的读指令列表
     *
     * @param mainData
     */
    List<AbstractIotCmdReq> generateReadCmdListByMainData(MainDataDTO mainData) {
        if (mainData == null || mainData.getDeviceList() == null || mainData.getDeviceList().isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("[config] 调用generateReadCmdListByMainData生成读指令失败，失败原因：DTU下面绑定的所有子设备不存在");
            }
            return Collections.emptyList();
        }
        // 1、按通讯协议分组，按通讯地址排序
        List<DtuDeviceDTO> deviceLst = mainData.filter(mainData);
        if (deviceLst.size() > 0) {
            //TODO:目前仅modbus RTU
            Map<String, List<DtuDeviceDTO>> protocolMap = deviceLst.stream()
                    // 按通讯协议分组
                    .collect(Collectors.groupingBy(DtuDeviceDTO::getCommcPrcl, Collectors.toList()));
            // 2、生成读指令列表
            return protocolMap.entrySet().stream()
                    // 生成读指令
                    .map(entry -> generateReadCmdListByProtocol(mainData, entry.getKey(), entry.getValue()))
                    // Stream<List<AbstractIotCmdReq>> 转换成 Stream<AbstractIotCmdReq>
                    .flatMap(Collection::stream)
                    // 转成成List<AbstractIotCmdReq>
                    .collect(Collectors.toList());
        } else {
            if (log.isWarnEnabled()) {
                log.warn(Log.context(mainData.getGatewaySn()) + "[config] 经过对设备过滤后，能够生成读指令的设备数量为0");
            }
            return Collections.emptyList();
        }
    }

    /**
     * 按协议维度，生成读指令列表
     *
     * @param mainData
     * @param communicationProtocol
     * @param protocolDeviceList
     * @return
     */
    List<AbstractIotCmdReq> generateReadCmdListByProtocol(MainDataDTO mainData, String communicationProtocol,
                                                          List<DtuDeviceDTO> protocolDeviceList) {
        ProtocolTypeEnum protocolType = ProtocolTypeEnum.getProtocolType(communicationProtocol);
        if (protocolType == null) {
            // warn protocolType is null
            List<DtuDeviceDTO> devList4Log = getDeviceList4Log(protocolDeviceList);
            log.warn(Log.context(mainData.getGatewaySn()) + "[config] 不支持的通讯协议,未找到枚举值！ 通讯协议: {}, devices: {}",
                    communicationProtocol, JsonUtils.writeValueAsString(devList4Log));
            return Collections.emptyList();
        }
        IotProtocolCodec codec = IotProtocolCodecFactory.getInstance(protocolType);
        if (codec == null) {
            // warn IotProtocolCodec is null
            List<DtuDeviceDTO> devList4Log = getDeviceList4Log(protocolDeviceList);
            log.warn(Log.context(mainData.getGatewaySn()) + "[config] 不支持的通讯协议, 未能实例化编解码器！通讯协议: {}, devices: {}",
                    communicationProtocol, JsonUtils.writeValueAsString(devList4Log));
            return Collections.emptyList();
        }
        MainDataDTO protocolMainData = new MainDataDTO();
        BeanUtils.copyProperties(mainData, protocolMainData);
        protocolMainData.setDeviceList(protocolDeviceList);
        try {
            return codec.generateReadCmdListByMainData(protocolMainData);
        } catch (Exception e) {
            log.error(Log.context(mainData.getGatewaySn()) + "[config] 读指令队列生成异常！", e);
            return Collections.emptyList();
        }
    }

    /**
     * 生成写指令列表
     *
     * @param controlCmdDTO 下行指令对象
     * @return java.util.List<com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq>
     * @author Mr.Jia
     * @date 2022/7/23 10:22 AM
     */
    @Override
    public List<AbstractIotCmdReq> generateWriteCmdListByProtocol(ControlCmdDTO controlCmdDTO) {
        String communicationProtocol = controlCmdDTO.getCommcPrcl();
        String gatewaySn = controlCmdDTO.getGatewaySn();
        DtuDeviceDTO device = controlCmdDTO.getDeviceDTO();
        ProtocolTypeEnum protocolType = ProtocolTypeEnum.getProtocolType(communicationProtocol);
        if (protocolType == null) {
            log.warn(IotChannelContextUtil.Log.context(gatewaySn) + "[config] 不支持的通讯协议,未找到枚举值！ 通讯协议: {}, devices: {}",
                    communicationProtocol, JsonUtils.writeValueAsString(device));
            return Collections.emptyList();
        }
        //
        IotProtocolCodec codec = IotProtocolCodecFactory.getInstance(protocolType);
        if (codec == null) {
            log.warn(
                    IotChannelContextUtil.Log.context(gatewaySn) + "[config] 不支持的通讯协议, 未能实例化编解码器！通讯协议: {}, devices: {}",
                    communicationProtocol, JsonUtils.writeValueAsString(device));
            return Collections.emptyList();
        }
        try {
            return codec.generateWriteCmdListByCmdData(controlCmdDTO);
        } catch (Exception e) {
            log.error(IotChannelContextUtil.Log.context(gatewaySn) + "[config] 写指令队列生成异常！", e);
            return Collections.emptyList();
        }
    }

    /**
     * 拷贝设备列表，删除测点信息
     *
     * @param devices
     * @return
     */
    List<DtuDeviceDTO> getDeviceList4Log(List<DtuDeviceDTO> devices) {
        if (devices == null || devices.isEmpty()) {
            return Collections.emptyList();
        }
        return devices.stream().map(device -> {
            DtuDeviceDTO result = new DtuDeviceDTO();
            BeanUtils.copyProperties(device, result);
            result.setPointInfo(null);
            return result;
        }).collect(Collectors.toList());
    }

    @Override
    public void refreshAllMainDataIf() {
        Map<String, MainDataDTO> allMainData = IotGlobalContextUtil.MainData.getAllMainData();
        List<String> refreshList = new LinkedList<>();
        try {
            for (String gatewaySn : allMainData.keySet()) {
                if (refreshMainDataIf(gatewaySn)) {
                    refreshList.add(gatewaySn);
                }
            }
        } catch (Exception e) {
            log.error("[config] 刷新配置异常！", e);
        }
        if (log.isInfoEnabled()) {
            log.info("[config] 刷新配置数据，已连接DTU数量: {}, 本次更新数量: {}, 本次更新清单: {}", allMainData.size(), refreshList.size(),
                    refreshList.toString());
        }
    }

    private MainDataDTO processPointInfo(String gatewaySn, List<DtuPointInfoDTO> pointList) {
        if (StringUtils.isEmpty(gatewaySn) || pointList == null || pointList.size() == 0) {
            return null;
        }
        MainDataDTO mainData = new MainDataDTO();
        mainData.setGatewaySn(gatewaySn);
        if (pointList.get(0).getStationId() != null) {
            // FIXME @nixiaolin DTU网关所属系统与子设备所属系统可能不相同
            mainData.setStationId(pointList.get(0).getStationId());
        }

        List<DtuDeviceDTO> deviceList = new ArrayList<>();
        for (DtuPointInfoDTO pointInfo : pointList) {
            DtuDeviceDTO deviceDTO = new DtuDeviceDTO();
            BeanUtils.copyProperties(pointInfo, deviceDTO);
            deviceDTO.setStationId(pointInfo.getStationId());
            deviceDTO.setTrdPtyCode(pointInfo.getTrdPtyCode());
            deviceList.add(deviceDTO);
        }

        mainData.setDeviceList(deviceList);
        return mainData;
    }


    /**
     * 转换解析类型
     *
     * @param parsingMode
     * @param bitOffset
     * @return
     */
    private static String transferDataType(String parsingMode, int bitOffset) {
        // 解析方式,0-BIT位，1-16位无符号整型，2-16位有符号整型，3-32位无符号整型，4-32位有符号整型，5-32位IEEE浮点型，6-32位浮点型，7-64位长浮点型，8-64位Long型，99-其他
        /**
         * 数据类型/modbus数据类型
         *  case "bit0": BIT位 0
         *  case "bit1": BIT位 1
         *  case "bit2": BIT位 2
         *  case "bit3": BIT位 3
         *  case "bit4": BIT位 4
         *  case "bit5": BIT位 5
         *  case "bit6": BIT位 6
         *  case "bit7": BIT位 7
         *  case "bit8": BIT位 8
         *  case "bit9": BIT位 9
         *  case "bit10": BIT位 10
         *  case "bit11": BIT位 11
         *  case "bit12": BIT位 12
         *  case "bit13": BIT位 13
         *  case "bit14": BIT位 14
         *  case "bit15": BIT位 15
         *  case "int8": 8位byte
         *  case "uint8": 8位Sint
         *  case "int16": 16位有符号整型
         *  case "uint16":16位无符号整型
         *  case "int32": 32位有符号整型
         *  case "uint32": 32位无符号整型
         *  case "int48":
         *  case "int64": 64位Long型
         *  case "uint64": 64位ULong型
         *  case "float32": 32位浮点型
         *  case "float64": 64位长浮点型
         *  case "time48":
         *  case "time64":
         *  case "string": 字符串
         */
        String modBusDataType = "";

        String[] offset = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "0"};
        List<String> offsets = Arrays.asList(offset);
        switch (parsingMode) {
            case "0":
                if (offsets.contains(String.valueOf(bitOffset))) {
                    modBusDataType = "bit" + bitOffset;
                } else {
                    log.warn("类型转换异常，不支持此类型，parsingMode：{},bitOffset:{}", 0, bitOffset);
                }
                break;
            case "1":
                modBusDataType = "uint16";
                break;
            case "2":
                modBusDataType = "int16";
                break;
            case "3":
                modBusDataType = "uint32";
                break;
            case "4":
                modBusDataType = "int32";
                break;
            case "5":
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", 5);
                break;
            case "6":
                modBusDataType = "float32";
                break;
            case "7":
                modBusDataType = "float64";
                break;
            case "8":
                modBusDataType = "int64";
                break;
            case "9":
                modBusDataType = "uint64";
                break;
            case "10":
                modBusDataType = "int8";
                break;
            case "11":
                modBusDataType = "uint8";
                break;
            case "12":
//                modBusDataType = "string";
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", 12 + "(字符串)");
                break;
            case "13":
                // 不支持
                //modBusDataType = "SBCD";
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", 13 + "(SBCD)");
                break;
            case "14":
                // 不支持
                //modBusDataType = "LBCD";
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", 14 + "(LBCD)");
                break;
            case "99":
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", 99);
                break;
            default:
                log.warn("类型转换异常，不支持此类型，parsingMode：{}", parsingMode);
        }
        return modBusDataType;
    }


    /**
     * 转换地址块为功能码
     * @param c
     * @return
     */
    private static String transFunctionCode(char c) {

        String functionCode = "";
        switch (c) {
            case '0':
                functionCode = "01";
                break;
            case '1':
                functionCode = "02";
                break;
            case '4':
                functionCode = "03";
                break;

            case '3':
                functionCode = "04";
                break;

            default:
                log.warn("块地址类型转换功能码异常，不支持此类型，blockAddr：{}", c);
        }
        return functionCode;
    }
}
