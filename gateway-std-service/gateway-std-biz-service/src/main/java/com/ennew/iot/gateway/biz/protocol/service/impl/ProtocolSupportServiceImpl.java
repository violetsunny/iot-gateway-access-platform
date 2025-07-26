package com.ennew.iot.gateway.biz.protocol.service.impl;

import cn.enncloud.iot.gateway.config.connectors.ProtocolConfig;
import cn.enncloud.iot.gateway.context.DeviceContext;
import cn.enncloud.iot.gateway.protocol.Protocol;
import cn.enncloud.iot.gateway.protocol.enums.ProtocolTypeEnum;
import cn.enncloud.iot.gateway.protocol.loader.jar.JarProtocolInitializer;
import cn.enncloud.iot.gateway.protocol.loader.script.ScriptProtocol;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ennew.iot.gateway.biz.protocol.enums.ProtocolDirectionTypeEnum;
import com.ennew.iot.gateway.biz.protocol.enums.ProtocolProvideTypeEnum;
import com.ennew.iot.gateway.biz.protocol.management.ProtocolSupportLoader;
import com.ennew.iot.gateway.biz.protocol.management.jar.JarProtocolSupportLoader;
import com.ennew.iot.gateway.biz.protocol.management.script.DeviceContextImpl;
import com.ennew.iot.gateway.biz.protocol.service.ProtocolSupportService;
import com.ennew.iot.gateway.client.enums.MessageType;
import com.ennew.iot.gateway.client.message.codec.DefaultTransport;
import com.ennew.iot.gateway.client.message.codec.DeviceMessageCodec;
import com.ennew.iot.gateway.client.protocol.ProtocolSupport;
import com.ennew.iot.gateway.client.protocol.model.Message;
import com.ennew.iot.gateway.client.protocol.model.OperationRequest;
import com.ennew.iot.gateway.client.protocol.model.ReportRequest;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.repository.ProtocolSupportManager;
import com.ennew.iot.gateway.core.repository.ProtocolSupportRepository;
import com.ennew.iot.gateway.dal.entity.ProtocolSupportEntity;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.kdla.framework.common.aspect.watch.StopWatchWrapper;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class ProtocolSupportServiceImpl implements ProtocolSupportService {

    @Autowired
    private ProtocolSupportRepository protocolSupportRepository;

    @Autowired
    private ProtocolSupportManager protocolSupportManager;


    @Autowired
    private DeviceContext deviceContext;

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "添加协议")
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ProtocolSupportBo bo) {
        return protocolSupportRepository.save(bo);
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "修改协议")
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ProtocolSupportBo bo) {
        return protocolSupportRepository.update(bo);
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "根据id查询协议")
    public ProtocolSupportResBo getById(String id) {
        return protocolSupportRepository.queryById(id);
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "查询协议列表")
    public List<ProtocolSupportResBo> query(ProtocolSupportQueryBo query) {
        return protocolSupportRepository.query(query);
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "协议分页查询")
    public PageResponse<ProtocolSupportResBo> queryPage(ProtocolSupportPageQueryBo queryPage) {
        return protocolSupportRepository.queryPage(queryPage);
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "删除协议")
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        boolean res = protocolSupportRepository.delete(id);
        protocolSupportManager.remove(id);
        return res;
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "发布协议")
    @Transactional(rollbackFor = Exception.class)
    public boolean deploy(String id) {
        ProtocolSupportDefinition definition = ProtocolSupportDefinition.toDeployDefinition(protocolSupportRepository.queryById(id));
        //将jar包下载到本地
//        ProtocolSupport load = loader.load(definition);
//        try {
//            load.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        boolean res = protocolSupportRepository.update(Wrappers.lambdaUpdate(ProtocolSupportEntity.class).set(ProtocolSupportEntity::getState, 1).set(ProtocolSupportEntity::getUpdateTime, new Date()).eq(ProtocolSupportEntity::getId, id));
        protocolSupportManager.save(definition);
        return res;
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "取消发布协议")
    @Transactional(rollbackFor = Exception.class)
    public boolean unDeploy(String id) {
        ProtocolSupportResBo bo = getById(id);
        boolean res = protocolSupportRepository.update(Wrappers.lambdaUpdate(ProtocolSupportEntity.class).set(ProtocolSupportEntity::getState, 0).set(ProtocolSupportEntity::getUpdateTime, new Date()).eq(ProtocolSupportEntity::getId, id));
        protocolSupportManager.save(ProtocolSupportDefinition.toUnDeployDefinition(bo));
        return res;
    }

    @Override
    @StopWatchWrapper(logHead = "ProtocolSupport", msg = "测试协议脚本")
    @Transactional(rollbackFor = Exception.class)
    public String runProtocolScript(String inputMessage, String protocolId) {

        // 根据协议id获取协议定义
        ProtocolSupportResBo protocolSupportResBo = protocolSupportRepository.queryById(protocolId);
        if (Objects.isNull(protocolSupportResBo)) {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "协议不存在:" + protocolId);
        }

        boolean validObject = JSONObject.isValidObject(inputMessage);
        if (!validObject) {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "输入参数不是json对象:");
        }

        ProtocolSupportDefinition definition = new ProtocolSupportDefinition();
        BeanUtils.copyProperties(protocolSupportResBo, definition, ProtocolSupportDefinition.class);
        definition.setProvider(protocolSupportResBo.getType());


        if (definition.getProvider().equals(ProtocolProvideTypeEnum.SCRIPT.getName())) {
            return runScriptParse(definition, protocolSupportResBo.getWay(), inputMessage);

        } else if (definition.getProvider().equals(ProtocolProvideTypeEnum.JAR.getName())) {
            return runJarParse(definition, protocolSupportResBo.getWay(), inputMessage);

        } else {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "脚本解析实现类型错误:" + definition.getProvider());
        }
    }

    @Override
    @Transactional
    public SingleResponse<String> saveReplica(ProtocolReplicaBo protocolReplicaBo) {
        String replicaId = "";
        // 有无协议id
        if (StringUtils.isEmpty(protocolReplicaBo.getId())) {
            //新增协议副本
            ProtocolSupportBo bo = new ProtocolSupportBo();
            replicaId = UUID.randomUUID().toString().replace("-", "");
            bo.setId(replicaId);
            bo.setName("script协议副本");
            bo.setType("script");
            bo.setWay(protocolReplicaBo.getWay());
            bo.setDescription("新增script协议副本");
            bo.setConfiguration(protocolReplicaBo.getConfiguration());
            bo.setIsTemplate((byte) 1);
            bo.setState((byte) 0);
            save(bo);
        } else {
            //选取协议模板
            ProtocolSupportResBo template = getById(protocolReplicaBo.getId());
            //判断是否为模板协议
            if (template.getIsTemplate() == (byte) 0) {
                //生成副本
                ProtocolSupportBo bo = new ProtocolSupportBo();
                BeanUtils.copyProperties(template, bo);
                replicaId = UUID.randomUUID().toString().replace("-", "");
                bo.setId(replicaId);
                bo.setConfiguration(protocolReplicaBo.getConfiguration());
                bo.setWay(protocolReplicaBo.getWay());
                bo.setDescription(template.getId() + "的协议副本");
                bo.setIsTemplate((byte) 1);
                bo.setState((byte) 0);
                save(bo);
            } else {
                //更新副本
                replicaId = protocolReplicaBo.getId();
                ProtocolSupportBo bo = new ProtocolSupportBo();
                BeanUtils.copyProperties(template, bo);
                bo.setConfiguration(protocolReplicaBo.getConfiguration());
                bo.setWay(protocolReplicaBo.getWay());
                bo.setState((byte) 0);
                update(bo);
            }
        }
        // 发布
        if (deploy(replicaId)) {
            return SingleResponse.buildSuccess(replicaId);
        } else {
            return SingleResponse.buildFailure("400", "发布失败");
        }
    }


    /**
     * jar包解析运行
     * {
     *     "provider":"类名",
     *     "location":"jar包地址"
     * }
     * @param definition
     * @param inputMessage
     * @return
     */
    private String runJarParse(ProtocolSupportDefinition definition, Integer way, String inputMessage) {
//        definition.setId("111");
//        definition.setName("test");
//
//        String config = "{\n" +
//                "  \"provider\": \"com.ennew.iot.gateway.protocol.HttpProtocolSupportProvider\",\n" +
//                "  \"location\": \"D:/enn/iot-protocols/gateway-protocol-json-demo/target/gateway-protocol-json-demo-1.0-SNAPSHOT.jar\"\n" +
//                " \n" +
//                "}";
//
//        definition.setConfiguration(JSONObject.parseObject(config));
//        JarProtocolSupportLoader jarProtocolSupportLoader = new JarProtocolSupportLoader();
//        DeviceMessageCodec messageCodec;
//        try (ProtocolSupport load = jarProtocolSupportLoader.load(definition)) {
//
//            messageCodec = load.getMessageCodec(DefaultTransport.HTTP);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        JSONObject payload = JSONObject.parseObject(inputMessage);
//        String result = null;
//        if (way.equals(ProtocolDirectionTypeEnum.UP.getCode())) {
//            ReportRequest reportRequest = new ReportRequest();
//
//            reportRequest.setMessageType(MessageType.REPORT_REQ);
//            reportRequest.setTimeStamp(System.currentTimeMillis());
//            reportRequest.setMetric(payload);
//
//            List<? extends Message> decode = messageCodec.decode(JSONObject.toJSONBytes(reportRequest));
//            result = JSONObject.toJSONString(decode);
//        } else if (way.equals(ProtocolDirectionTypeEnum.DOWN.getCode())) {
//
//            OperationRequest operationRequest = new OperationRequest();
//            operationRequest.setTimeStamp(System.currentTimeMillis());
//            operationRequest.setParam(payload);
//            byte[] encode = messageCodec.encode(operationRequest);
//            result = ByteBufUtil.hexDump(encode);
//        } else {
//            throw new BizException(ErrorCode.PARAMETER_ERROR, "脚本解析方向类型错误:" + way);
//        }

        JSONObject payload = JSONObject.parseObject(inputMessage);
        String mainClass = Optional.ofNullable(definition.getConfiguration())
                .map(obj -> String.valueOf(obj.get("provider")))
                .orElse(null);
        String path = Optional.ofNullable(definition.getConfiguration())
                .map(obj -> String.valueOf(obj.get("location")))
                .orElse(null);
        ProtocolConfig config = new ProtocolConfig();
        config.setMainClass(mainClass);
        config.setPath(path);
        config.setName(definition.getId());
        config.setType(ProtocolTypeEnum.JAR.getName());

        JarProtocolInitializer jarProtocolInitializer = new JarProtocolInitializer();
        Protocol protocol = jarProtocolInitializer.init(config);

        String result = null;
        // 下行-编码
        if (way.equals(ProtocolDirectionTypeEnum.DOWN.getCode())) {
            cn.enncloud.iot.gateway.message.OperationRequest parseObject = JSONObject.parseObject(inputMessage, cn.enncloud.iot.gateway.message.OperationRequest.class);

            byte[] encode = new byte[0];
            try {
                encode = protocol.encode(parseObject);
            } catch (Exception e) {
                log.warn("jar协议encode执行异常：{}", e.getMessage());
                throw new BizException(ErrorCode.BIZ_ERROR, "脚本协议encode执行异常:" + e.getMessage());
            }

            result = new String(encode, StandardCharsets.UTF_8);

            // 上行解码
        } else if (way.equals(ProtocolDirectionTypeEnum.UP.getCode())) {
            Object[] paramsObj = new Object[0];
            byte[] dataBytes = new byte[0];
            try {
                paramsObj = buildParams(payload);
                dataBytes = JSONObject.toJSONBytes(payload.get("data"));
            } catch (Exception e) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "jar协议调试参数异常:" + e.getMessage());
            }

            List<? extends cn.enncloud.iot.gateway.message.Message> data = null;
            try {
                data = protocol.decodeMulti(dataBytes, paramsObj);
            } catch (Exception e) {
                log.warn("jar协议decode执行异常：{}", e.getMessage());
                throw new BizException(ErrorCode.BIZ_ERROR, "jar协议decode执行异常:" + e.getMessage());
            }


            result = JSONObject.toJSONString(data);
        } else {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "脚本解析方向类型错误:" + way);
        }
        return result;

    }

    /**
     * 脚本解析运行
     * {
     *     "script":"js脚本",
     *     "transport":"传输",
     *     "protocol":"协议名称"
     * }
     * @param definition
     * @param inputMessage
     * @return
     */
    public String runScriptParse(ProtocolSupportDefinition definition, Integer way, String inputMessage) {
//        SpringContextUtil springContextUtil = new SpringContextUtil();
//        ProtocolSupport protocolSupport = null;
//        try (ProtocolSupportProvider supportProvider = new ScriptProtocolSupportProvider(definition)) {
//            protocolSupport = supportProvider.create(springContextUtil);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 脚本默认配置为TCP通信
//        DeviceMessageCodec messageCodec = protocolSupport.getMessageCodec(DefaultTransport.TCP);

        String script = String.valueOf(definition.getConfiguration().get("script"));

        ScriptProtocol scriptProtocol = null;
        try {
            scriptProtocol = new ScriptProtocol(script);
            scriptProtocol.setDeviceContext(deviceContext);
        } catch (Exception e) {
            log.warn("脚本协议初始化异常：{}", e.getMessage());
            throw new BizException(ErrorCode.BIZ_ERROR, "脚本js编译异常:" + e.getMessage());
        }


        JSONObject payload = JSONObject.parseObject(inputMessage);

        String result = null;
        // 下行-编码
        if (way.equals(ProtocolDirectionTypeEnum.DOWN.getCode())) {

            cn.enncloud.iot.gateway.message.OperationRequest parseObject = JSONObject.parseObject(inputMessage, cn.enncloud.iot.gateway.message.OperationRequest.class);
//            byte[] encode = messageCodec.encode(parseObject);

            byte[] encode = new byte[0];
            try {
                encode = scriptProtocol.encode(parseObject);
            } catch (Exception e) {
                log.warn("脚本协议encode执行异常：{}", e.getMessage());
                throw new BizException(ErrorCode.BIZ_ERROR, "脚本协议encode执行异常:" + e.getMessage());
            }

            result = new String(encode, StandardCharsets.UTF_8);

            // 上行解码
        } else if (way.equals(ProtocolDirectionTypeEnum.UP.getCode())) {
//            ReportRequest reportRequest = new ReportRequest();
//            reportRequest.setTimeStamp(System.currentTimeMillis());
//            reportRequest.setMetric(payload);
//            List<? extends Message> decode = messageCodec.decode(JSONObject.toJSONBytes(reportRequest));

            Object[] paramsObj = new Object[0];
            byte[] dataBytes = new byte[0];
            try {
                paramsObj = buildParams(payload);
                dataBytes = JSONObject.toJSONBytes(payload.get("data"));
            } catch (Exception e) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "脚本协议调试参数异常:" + e.getMessage());
            }

            List<? extends cn.enncloud.iot.gateway.message.Message> data = null;
            try {
                data = scriptProtocol.decodeMulti(dataBytes, paramsObj);
            } catch (Exception e) {
                log.warn("脚本协议decode执行异常：{}", e.getMessage());
                throw new BizException(ErrorCode.BIZ_ERROR, "脚本协议decode执行异常:" + e.getMessage());
            }


            result = JSONObject.toJSONString(data);
        } else {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "脚本解析方向类型错误:" + way);
        }

        return result;
    }

    @NotNull
    private static Object[] buildParams(JSONObject payload) {
        JSONArray params = payload.getJSONArray("params");
        if (Objects.isNull(params)) {
            return new String[]{};
        }
        Object[] paramsObj = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            Object obj = params.get(i);
            paramsObj[i] = obj;
        }
        return paramsObj;
    }

    @Override
    public boolean isExistName(String name) {
        return !CollectionUtils.isEmpty(protocolSupportRepository.queryByName(name));
    }

    public static void main(String[] args) {
//        OperationRequest eventRequest = new OperationRequest();
//        String s = JSONObject.toJSONString(eventRequest);
//
//        String byteStr = "67401501621521302023020122a0012c5a84d0a03c86487528718b06dfa003e54a9ddce9fb3cb885d6eaf9247213d36936bf9ecfa1052cd56ad207ede17828c065d92b1ebaaa24914d09770fbffb2de54a9ddce9fb3cb885d6eaf9247213d36936bf9ecfa1052cd56ad207ede17828c065d92b1ebaaa24914d09770fbffb2de54a9ddce9fb3cb885d6eaf9247213d36936bf9ecfa1052cd56ad207ede17828c065d92b1ebaaa24914d09770fbffb2de54a9ddce9fb3cb885d6eaf9247213d36936bf9ecfa1052cd56ad207ede17828c065d92b1ebaaa24914d09770fbffb2dfa7684ddeb0f88a9ff262d47e6bb18191de52f128beec6f4d93272cb7053dbab2e93a3dd2885098612414b7c29acf93fe143e20b7124efef30f9de54432fb3421de52f128beec6f4d93272cb7053dbab2e93a3dd2885098612414b7c29acf93fe143e20b7124efef30f9de54432fb3421de52f128beec6f4d93272cb7053dbab2e93a3dd2885098612414b7c29acf93fe143e20b7124efef30f9de54432fb3421de52f128beec6f4d93272cb7053dbab81667f9f4ef65813f52117327abebf8d995c35fafe03d636877a4fbb4b45016de12eed";
//        byte[] bytes = ByteBufUtil.decodeHexDump(byteStr);
//        System.out.println(bytes);
//
//        boolean validObject = JSONObject.isValidObject(s.substring(1));
//        System.out.println(validObject);

//        String input = " {\n" +
//                "      \"data\": {\n" +
//                "        \"code\": 200,\n" +
//                "        \"message\": \"\",\n" +
//                "        \"data\": {\n" +
//                "            \"ie\": \"YB69XXXXXXXXXX\",\n" +
//                "            \"online\": true,\n" +
//                "            \"onlineTime\": \"2024-03-27 10:39:29\",\n" +
//                "            \"data\": {\n" +
//                "                \"type\": 0,\n" +
//                "                \"at\": \"30.4\",\n" +
//                "                \"ah\": \"57.8\",\n" +
//                "                \"mode\": 0,\n" +
//                "                \"ppos\": 92,\n" +
//                "                \"psize\": 120,\n" +
//                "                \"csq\": 25\n" +
//                "            },\n" +
//                "            \"onlineTimeStr\": \"2024-03-27 10:39:29\"\n" +
//                "        },\n" +
//                "        \"success\": true\n" +
//                "    },\n" +
//                "        \"params\": [ \"/aaaaa\"]\n" +
//                "        \n" +
//                "    }";
//        JSONObject payload = JSONObject.parseObject(input);
//        Object params = payload.get("params");
//        Object params1 = payload.getJSONArray("params");
//        byte[] data1 = payload.getString("data").getBytes(StandardCharsets.UTF_8);
//        System.out.println(params);
//
    }

}
