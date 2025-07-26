package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.ctwing.CtwingCloudServer;
import com.ennew.iot.gateway.biz.protocol.management.ProtocolSupportLoaderProvider;
import com.ennew.iot.gateway.biz.protocol.service.ProtocolSupportService;
import com.ennew.iot.gateway.biz.protocol.supports.ProtocolSupports;
import com.ennew.iot.gateway.common.enums.ProtocolTypeEnum;
import com.ennew.iot.gateway.common.enums.ProtocolWayEnum;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.web.converter.ProtocolSupportVoConverter;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import com.ennew.iot.gateway.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.log.catchlog.CatchAndLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/protocol")
@Tag(name = "协议管理")
@Slf4j
@CatchAndLog
public class ProtocolSupportController {

    @Autowired
    private ProtocolSupportService protocolSupportService;

    @Autowired
    private List<ProtocolSupportLoaderProvider> providers;

    @Autowired
    private ProtocolSupports protocolSupports;

    @Autowired
    private ProtocolSupportVoConverter protocolSupportVoConverter;

    @PostMapping("/add")
    @Operation(summary = "新增协议")
    public SingleResponse<Boolean> add(@RequestBody @Validated(ValidationGroups.Insert.class) ProtocolSupportCmdVo cmd) {
        ProtocolSupportBo bo = protocolSupportVoConverter.fromProtocolSupport(cmd);
        if (protocolSupportService.isExistName(cmd.getName())) {
            return SingleResponse.buildFailure("10001", "名称已存在，请换一个试试");
        }
        return SingleResponse.buildSuccess(protocolSupportService.save(bo));
    }

    @PostMapping("/save/replica")
    @Operation(summary = "保存JS协议副本")
    public SingleResponse<String> saveReplica(@RequestBody @Validated(ValidationGroups.Insert.class) ProtocolReplicaVo protocolReplicaVo) {
        return protocolSupportService.saveReplica(BeanUtil.copyProperties(protocolReplicaVo, ProtocolReplicaBo.class));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改数据")
    public SingleResponse<Boolean> update(@PathVariable String id, @RequestBody @Validated(ValidationGroups.Update.class) ProtocolSupportCmdVo cmd) {
        ProtocolSupportBo bo = protocolSupportVoConverter.fromProtocolSupport(cmd);
        bo.setId(id);
        return SingleResponse.buildSuccess(protocolSupportService.update(bo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询")
    public SingleResponse<ProtocolSupportResVo> getById(@PathVariable String id) {
        return SingleResponse.buildSuccess(protocolSupportVoConverter.toProtocolSupportRes(protocolSupportService.getById(id)));
    }

    @PostMapping("/query")
    @Operation(summary = "查询列表")
    public MultiResponse<ProtocolSupportResVo> query(@RequestBody ProtocolSupportQueryVo query) {
        ProtocolSupportQueryBo queryBo = protocolSupportVoConverter.fromProtocolSupportQuery(query);
        queryBo.setIsTemplate((byte) 0);
        List<ProtocolSupportResBo> resBos = protocolSupportService.query(queryBo);
        return MultiResponse.of(protocolSupportVoConverter.toProtocolSupportResList(resBos));
    }

    @PostMapping("/queryPage")
    @Operation(summary = "分页查询")
    public PageResponse<ProtocolSupportResVo> queryPage(@RequestBody ProtocolSupportPageQueryVo pageQuery) {
        ProtocolSupportPageQueryBo queryPageBo = protocolSupportVoConverter.fromProtocolSupportPageQuery(pageQuery);
        queryPageBo.setIsTemplate((byte) 0);
        PageResponse<ProtocolSupportResBo> resPageBo = protocolSupportService.queryPage(queryPageBo);
        return PageResponse.of(protocolSupportVoConverter.toProtocolSupportResList(resPageBo.getData()), resPageBo.getTotalCount(), resPageBo.getPageSize(), resPageBo.getPageNum());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除")
    public SingleResponse<Boolean> delete(@PathVariable String id) {
        return SingleResponse.buildSuccess(protocolSupportService.delete(id));
    }

    @PostMapping("/{id}/deploy")
    @Operation(summary = "发布协议")
    public SingleResponse<Boolean> deploy(@PathVariable String id) {
        return SingleResponse.buildSuccess(protocolSupportService.deploy(id));
    }

    @PostMapping("/{id}/unDeploy")
    @Operation(summary = "取消发布")
    public SingleResponse<Boolean> unDeploy(@PathVariable String id) {
        return SingleResponse.buildSuccess(protocolSupportService.unDeploy(id));
    }

    @GetMapping("/providers")
    @Operation(summary = "获取当前支持的协议类型")
    public MultiResponse<String> getProviders() {
        return MultiResponse.buildSuccess(providers.stream().map(ProtocolSupportLoaderProvider::getProvider).collect(Collectors.toList()));
    }

    @GetMapping("/enum/{type}")
    @Operation(summary = "获取下拉框枚举 type:协议类型,way:上下行")
    public SingleResponse<Map<String, Integer>> getEnum(@PathVariable String type) {
        Map<String, Integer> map = new HashMap<>();
        if ("type".equalsIgnoreCase(type)) {
            map = Arrays.stream(ProtocolTypeEnum.values()).collect(Collectors.toMap(ProtocolTypeEnum::getName, ProtocolTypeEnum::getCode));
        } else if ("way".equalsIgnoreCase(type)) {
            map = Arrays.stream(ProtocolWayEnum.values()).collect(Collectors.toMap(ProtocolWayEnum::getName, ProtocolWayEnum::getCode));
        }
        return SingleResponse.buildSuccess(map);
    }

    @PostMapping("/runProtocolScript")
    @Operation(summary = "运行测试协议脚本")
    public SingleResponse<String> runProtocolScript(@RequestBody ProtocolRunVo runVo) {
        return SingleResponse.buildSuccess(protocolSupportService.runProtocolScript(runVo.getInputMessage(), runVo.getId()));
    }

    @Autowired
    private CtwingCloudServer ctwingCloudServer;

    @PostMapping("/testCtwing")
    @Operation(summary = "testCtwing")
    public SingleResponse<String> testCtwing(String inputMessage) {
        try {
            ctwingCloudServer.dealCloudData(inputMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return SingleResponse.buildSuccess();
    }

}
