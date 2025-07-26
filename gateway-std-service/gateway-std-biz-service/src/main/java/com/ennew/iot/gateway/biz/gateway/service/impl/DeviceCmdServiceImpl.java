package com.ennew.iot.gateway.biz.gateway.service.impl;

import cn.enncloud.iot.gateway.message.OperationRequest;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ennew.iot.gateway.biz.gateway.enums.CmdSendStatusEnum;
import com.ennew.iot.gateway.biz.gateway.enums.CmdTypeEnum;
import com.ennew.iot.gateway.biz.gateway.service.DeviceCmdService;
import com.ennew.iot.gateway.client.protocol.model.OperationResponse;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.converter.DeviceCmdBoConverter;
import com.ennew.iot.gateway.core.service.KafkaProducer;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdAckEntity;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import com.ennew.iot.gateway.dal.mapper.EnnDownCmdAckMapper;
import com.ennew.iot.gateway.dal.mapper.EnnDownCmdRecordMapper;
import com.ennew.iot.gateway.integration.device.PhysicalModelClient;
import com.ennew.iot.gateway.integration.device.model.req.IotDevPhysicalModelMeasureQueryReq;
import com.ennew.iot.gateway.integration.device.model.resp.IotDevPhysicalModelIdResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.kdla.framework.dto.PageResponse;

import java.util.*;


@Slf4j
@Service
public class DeviceCmdServiceImpl implements DeviceCmdService {


    @Autowired
    private EnnDownCmdAckMapper ennDownCmdAckMapper;

    @Autowired
    private EnnDownCmdRecordMapper ennDownCmdRecordMapper;


    @Autowired
    private RedisService redisService;


    @Autowired
    private PhysicalModelClient deviceClient;


    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private DeviceCmdBoConverter deviceCmdBoConverter;


    @Value("${ennew.iot.topic.commandTopic:device_command_topic}")
    private String commandTopic;
    @Value("${ennew.iot.topic.cmdRespTopic:device_cmd_resp_topic}")
    private String cmdRespTopic;

    @Override
    public Boolean sendCmdSet(ControlCmdSetRequestBO request) {

        String type = CmdTypeEnum.SET.getCode();


        String devId = request.getDev();
        //appId为调用方应用id, 入参中写作source, 仅入库记录
        String appId = request.getSource();
        //构建下行消息json
        String sysId = request.getSysId();
        String tenantId = request.getTenantId();

        String m = request.getM();
        Object v = request.getV();
        String serviceCode = request.getServiceCode();
        try {
            checkCmdByPhysicalModel(devId, m);
        } catch (Exception e) {
            throw new RuntimeException("设备校验异常," + e.getMessage());
        }

        //TODO：enn mqtt设备专有逻辑
        String gatewayDeviceId = redisService.getParentIdFromRedis(devId);
        //云边网关使用的pkey是网关设备的product code
        String productId = redisService.getProductIdFromRedis(gatewayDeviceId);
        String pKey = redisService.getProductCodeFromRedis(productId);
        String sn = redisService.getSnFromRedis(devId);

        // 信令下发序号
        String seq = generateSeqSn(devId);


        cn.enncloud.iot.gateway.message.OperationRequest operationRequest = new OperationRequest();
        operationRequest.setMessageId(seq);
        operationRequest.setDeviceId(devId);
        operationRequest.setSn(sn);
        operationRequest.setFunction(type);
        operationRequest.setTimeStamp(request.getExpectTime() != null ? request.getExpectTime().getTime() : System.currentTimeMillis());

        HashMap<String, Object> params = new HashMap<>();
        params.put(m, v);
        params.put("sysId", sysId);
        params.put("serviceCode", serviceCode);

        // 有网关父设备时下发
        if (StringUtils.isNotBlank(gatewayDeviceId)) {
            params.put("gatewayDeviceId", gatewayDeviceId);
            params.put("pKey", pKey);
        }

        operationRequest.setParam(params);

        if (request.getExpectTime() == null) {
            // 立即发送 kafka
            //TODO:topic确认修改
            return kafkaProducer.send(commandTopic, JSONUtil.toJsonStr(operationRequest));

        } else {

            // 入库记录，待调度任务发送
            saveCmdRecord(seq, appId, devId, JSONUtil.toJsonStr(operationRequest), type, tenantId, request.getExpectTime(), serviceCode);

            return true;
        }


    }

    @Override
    public Boolean sendCmdService(ControlCmdServiceRequestBO request) {

        String type = CmdTypeEnum.SET.getCode();


        String devId = request.getDev();
        //appId为调用方应用id, 入参中写作source, 仅入库记录
        String appId = request.getSource();
        String sysId = request.getSysId();
        String tenantId = request.getTenantId();

        String serviceCode = request.getServiceCode();


        //TODO：子设备专有逻辑
        String gatewayDeviceId = redisService.getParentIdFromRedis(devId);
        //云边网关使用的pkey是网关设备的product code
        String productId = redisService.getProductIdFromRedis(gatewayDeviceId);
        String pKey = redisService.getProductCodeFromRedis(productId);
        String sn = redisService.getSnFromRedis(devId);

        // 信令下发序号
        String seq = generateSeqSn(devId);


        cn.enncloud.iot.gateway.message.OperationRequest operationRequest = new OperationRequest();
        operationRequest.setMessageId(seq);
        operationRequest.setDeviceId(devId);
        operationRequest.setSn(sn);
        operationRequest.setFunction(type);
        operationRequest.setTimeStamp(request.getExpectTime() != null ? request.getExpectTime().getTime() : System.currentTimeMillis());

        HashMap<String, Object> params = new HashMap<>(request.getInput());
        params.put("sysId", sysId);
        params.put("serviceCode", serviceCode);

        // 有网关父设备时下发
        if (StringUtils.isNotBlank(gatewayDeviceId)) {
            params.put("gatewayDeviceId", gatewayDeviceId);
            params.put("pKey", pKey);
        }

        operationRequest.setParam(params);

        if (request.getExpectTime() == null) {
            // 立即发送 kafka
            //TODO:topic确认修改
            return kafkaProducer.send(commandTopic, JSONUtil.toJsonStr(operationRequest));

        } else {
            // 入库记录，待调度任务发送
            saveCmdRecord(seq, appId, devId, JSONUtil.toJsonStr(operationRequest), type, tenantId, request.getExpectTime(), serviceCode);
            return true;
        }
    }


    @Override
    public PageResponse<ControlCmdHistoryBO> queryHistoryCmdByDev(ControlCmdHistoryRequestBO request) {
        String devId = request.getDev();
        String type = request.getType();
        String seq = request.getSeq();
        String source = request.getSource();
        String tenantId = request.getTenantId();
        Date timeRangeStart = request.getTimeRangeStart();
        Date timeRangeEnd = request.getTimeRangeEnd();
        String servicCode = request.getServiceCode();
        Page<EnnDownCmdRecordEntity> page = Page.of(request.getPageNumber(), request.getPageSize());
//        QueryWrapper recordQueryWrapper = new QueryWrapper();
//        recordQueryWrapper.eq(StringUtils.isNotBlank(devId), "r.dev_id", devId);
//        recordQueryWrapper.eq(StringUtils.isNotBlank(type), "r.cmd_type", type);
//        recordQueryWrapper.eq(StringUtils.isNotBlank(seq), "r.seq", seq);
//        recordQueryWrapper.eq(StringUtils.isNotBlank(source), "r.source", source);
//        recordQueryWrapper.eq(StringUtils.isNotBlank(tenantId), "r.tenant_id", tenantId);
        ennDownCmdRecordMapper.selectPageWithAcked(page, devId, type, seq, source, tenantId, timeRangeStart, timeRangeEnd, servicCode);
        List<ControlCmdHistoryBO> list = deviceCmdBoConverter.toCmdRecordRes(page.getRecords());
        return PageResponse.of(list, page.getTotal(), page.getSize(), page.getCurrent());

    }

    @Override
    public PageResponse<ControlCmdAckBO> queryCmdAck(ControlCmdAckListRequestBO request) {
        String devId = request.getDev();
        String seq = request.getSeq();
        Page<EnnDownCmdAckEntity> page = Page.of(request.getPageNumber(), request.getPageSize());
        QueryWrapper recordQueryWrapper = new QueryWrapper();
        recordQueryWrapper.eq(StringUtils.isNotBlank(devId), "dev_id", devId);
        recordQueryWrapper.eq(StringUtils.isNotBlank(seq), "seq", seq);
        ennDownCmdAckMapper.selectPage(page, recordQueryWrapper);
        List<EnnDownCmdAckEntity> records = page.getRecords();
        List<ControlCmdAckBO> controlCmdAckBOS = deviceCmdBoConverter.toCmdAckRes(records);
        return PageResponse.of(controlCmdAckBOS, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public Boolean updateCmdStatus(String msgId, String deviceId, String status) {


        EnnDownCmdRecordEntity ennDownCmdRecord = ennDownCmdRecordMapper.selectOne(

                Wrappers.<EnnDownCmdRecordEntity>update()
                        .lambda()
                        .eq(EnnDownCmdRecordEntity::getSeq, msgId)
                        .eq(EnnDownCmdRecordEntity::getDevId, deviceId));
        if (Objects.isNull(ennDownCmdRecord)) {
            throw new RuntimeException("设备信令信息不存在");
        }

        ennDownCmdRecord.setSendStatus(CmdSendStatusEnum.sent.getCode());
        ennDownCmdRecord.setSendTime(new Date());

        int i = ennDownCmdRecordMapper.updateById(ennDownCmdRecord);
        return i > 0;
    }


    private void saveCmdRecord(String seq, String source, String devId, String content, String type, String tenantId, Date expectSendTime, String serviceCode) {
        boolean immediate = expectSendTime == null;
        EnnDownCmdRecordEntity record = new EnnDownCmdRecordEntity();
        record.setSeq(seq);
        record.setSource(source);
        record.setTenantId(tenantId);
        record.setDevId(devId);
        record.setTransport("MQTT");
        record.setExpectTime(immediate ? null : expectSendTime);
        record.setContent(content);
        record.setCmdType(type);
        record.setSendType(immediate ? "immediate" : "timed");
        record.setSendStatus(immediate ? CmdSendStatusEnum.sent.getCode() : CmdSendStatusEnum.accepted.getCode());
        record.setCreateTime(new Date());
        record.setSendTime(immediate ? new Date() : null);
        record.setServiceCode(serviceCode);
        ennDownCmdRecordMapper.insert(record);
    }


    private String generateSeqSn(String deviceId) {

        return redisService.getTemplate().opsForValue().increment("cmdSeq:" + deviceId, 1).toString();
    }


    /**
     * 更新指令下发记录中是否回复字段
     *
     * @param operationResponse
     */
    public void updateDownCmdRecord(OperationResponse operationResponse) {

        Long aLong = ennDownCmdRecordMapper.selectCount(Wrappers.<EnnDownCmdRecordEntity>lambdaQuery()
                .eq(EnnDownCmdRecordEntity::getSeq, operationResponse.getMessageId())
                .eq(EnnDownCmdRecordEntity::getDevId, operationResponse.getDeviceId()));
        if (aLong < 0) {
            log.warn("设备指令回复未查询到记录,msg{}", JSONObject.toJSONString(operationResponse));
            return;
        }
        EnnDownCmdRecordEntity record = new EnnDownCmdRecordEntity();
        record.setAcked(true);
        record.setSeq(operationResponse.getMessageId());
        record.setDevId(operationResponse.getDeviceId());
        ennDownCmdRecordMapper.update(
                record,
                Wrappers.<EnnDownCmdRecordEntity>update()
                        .lambda()
                        .eq(EnnDownCmdRecordEntity::getSeq, operationResponse.getMessageId())
                        .eq(EnnDownCmdRecordEntity::getDevId, operationResponse.getDeviceId()));
    }


    /**
     * 保存回复信息
     *
     * @param operationResponse
     * @param record
     */
    public void saveDownCmdAck(OperationResponse operationResponse, EnnDownCmdRecordEntity record) {
        try {
            EnnDownCmdAckEntity ennDownCmdAck = new EnnDownCmdAckEntity();
            Date now = new Date();
            ennDownCmdAck.setAckTime(now);
            if (Objects.nonNull(record) && Objects.nonNull(record.getCmdType())) {
                ennDownCmdAck.setCmdType(record.getCmdType());
            }
            //TODO:返回的原始对象
            ennDownCmdAck.setContent(operationResponse.getResponse());

            ennDownCmdAck.setCreateTime(now);
            ennDownCmdAck.setSeq(operationResponse.getMessageId());
            ennDownCmdAck.setDevId(operationResponse.getDeviceId());
            if (Objects.nonNull(record)) {
                if (Objects.nonNull(record.getSendTime())) {
                    ennDownCmdAck.setSendTime(record.getSendTime());
                } else {
                    ennDownCmdAck.setSendTime(new Date());
                }
            }
            ennDownCmdAckMapper.insert(ennDownCmdAck);
        } catch (Exception e) {
            log.warn("信令回复保存处理异常:{}", e.getMessage());
        }
    }

    @Override
    public Boolean cmdResp(OperationResponse operationResponse) {
        updateDownCmdRecord(operationResponse);

        EnnDownCmdRecordEntity downCmdRecordEntity = ennDownCmdRecordMapper.selectOne(

                Wrappers.<EnnDownCmdRecordEntity>update()
                        .lambda()
                        .eq(EnnDownCmdRecordEntity::getSeq, operationResponse.getMessageId())
                        .eq(EnnDownCmdRecordEntity::getDevId, operationResponse.getDeviceId()));
        saveDownCmdAck(operationResponse, downCmdRecordEntity);
        return true;
    }


    @Value("${down.checkCmdSwitch:false}")
    boolean checkCmdSwitch;

    private void checkCmdByPhysicalModel(String devId, String m) throws Exception {
        if (!checkCmdSwitch) {
            return;
        }
        if (!checkDeviceOnline(devId)) {
            throw new Exception("设备离线, devId=" + devId);
        }
        if (!checkMeasureOfDevice(devId, m)) {
            throw new Exception("该设备测点不可写入, devId=" + devId + ", m=" + m);
        }
    }

    public boolean checkDeviceOnline(String devId) throws Exception {
        IotDevPhysicalModelIdResp<JSONObject> resp = deviceClient.getDeviceById(devId);
        if (!resp.isSuccess()) {
            throw new Exception("获取设备状态失败, devId=" + devId);
        }
        JSONObject data = resp.getData();
        if (data == null) {
            throw new Exception("设备不存在, devId=" + devId);
        }
        Integer state = data.getInteger("state");
        return state == 1;
    }

    //    private ConcurrentHashMap<String,Map> measureDefinitionCache = new ConcurrentHashMap<>();
    public boolean checkMeasureOfDevice(String devId, String m) throws Exception {
//        String cacheKey = devId+':'+m;
//        Map cache = measureDefinitionCache.get(cacheKey);
//        if(cache!=null){
//            return !(Boolean) cache.get("readOnly");
//        }
        IotDevPhysicalModelMeasureQueryReq req = IotDevPhysicalModelMeasureQueryReq.builder()
                .deviceId(devId).keyword(m).current(1).size(100).build();
        IotDevPhysicalModelIdResp<JSONObject> resp = deviceClient.getMeasureDefinition(req);
        if (!resp.isSuccess()) {
            throw new Exception("获取设备测点信息失败, devId=" + devId);
        }
        JSONObject data = resp.getData();
        JSONArray list = data.getJSONArray("list");
        //检查是否存在测点
        if (!list.isEmpty()) {
            Map measureDefinition = (Map) list.get(0);
            if (measureDefinition.get("code").equals(m)) {
//                measureDefinitionCache.put(cacheKey,measureDefinition);
                //检查测点是否可写
                return !(Boolean) measureDefinition.get("readOnly");
            }
        }
        throw new Exception("设备不包含该测点, devId=" + devId + ", m=" + m);

    }

}
