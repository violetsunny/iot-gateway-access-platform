package com.ennew.iot.gateway.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ennew.iot.gateway.biz.gateway.service.DeviceCmdService;
import com.ennew.iot.gateway.client.protocol.model.OperationResponse;
import com.ennew.iot.gateway.client.service.CmdApiFeignService;
import com.ennew.iot.gateway.core.bo.ControlCmdHistoryBO;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdAckEntity;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import com.ennew.iot.gateway.web.converter.DeviceCmdVoConverter;
import com.ennew.iot.gateway.web.vo.ControlCmdAckListRequestVo;
import com.ennew.iot.gateway.web.vo.ControlCmdHistoryRequestVo;
import com.ennew.iot.gateway.web.vo.ControlCmdServiceRequestVo;
import com.ennew.iot.gateway.web.vo.ControlCmdSetRequestVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.dto.exception.ErrorCode;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:26 2023/4/19
 */
@RestController
@RequestMapping("/down")
@Tag(name = "统一指令下行接口")
@Slf4j
@RequiredArgsConstructor
public class DeviceCmdController implements CmdApiFeignService {

    @Autowired
    private DeviceCmdService cmdService;
    private final DeviceCmdVoConverter deviceCmdVoConverter;


    @Operation(summary = "指令下发-测点写入")
    @PostMapping("/cmd/set")
    public SingleResponse sendCmdSet(@RequestBody ControlCmdSetRequestVo request) {
        log.info("接收下行指令cmd/set, {}", request);
        if (!request.checkValid()) {
            return SingleResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "错误的下行指令, 缺少必要参数");
        }
        Boolean aBoolean = cmdService.sendCmdSet(deviceCmdVoConverter.fromControlCmdSetRequest(request));
        if (aBoolean) {
            return SingleResponse.buildSuccess(aBoolean);
        } else {
            return SingleResponse.buildFailure(ErrorCode.BIZ_ERROR.getCode(), "信令发送失败！");
        }
    }


    @Operation(summary = "指令下发-服务命令")
    @PostMapping("/cmd/service")
    public SingleResponse sendCmdService(@RequestBody ControlCmdServiceRequestVo request) {
        log.info("接收下行指令cmd/service, {}", request);
        if (!request.checkValid()) {
            return SingleResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "错误的下行指令, 缺少必要参数");
        }
        Boolean aBoolean = cmdService.sendCmdService(deviceCmdVoConverter.fromControlCmdServiceRequest(request));
        if (aBoolean) {
            return SingleResponse.buildSuccess(aBoolean);
        } else {
            return SingleResponse.buildFailure(ErrorCode.BIZ_ERROR.getCode(), "信令发送失败！");
        }
    }


    @Operation(summary = "指令下发完成发送回调")
    @PostMapping("/cmd/sent")
    public SingleResponse sentCmdService(@RequestParam String msgId, String deviceId, String status) {

        Boolean aBoolean = cmdService.updateCmdStatus( msgId,  deviceId,  status);
        if (aBoolean) {
            return SingleResponse.buildSuccess(aBoolean);
        } else {
            return SingleResponse.buildFailure(ErrorCode.BIZ_ERROR.getCode(), "信令发送回调！");
        }
    }

    @Operation(summary = "指令回复")
    @PostMapping("/cmd/resp")
    public SingleResponse testResp(@RequestBody OperationResponse operationResponse) {

        Boolean aBoolean = cmdService.cmdResp(operationResponse);
        if (aBoolean) {
            return SingleResponse.buildSuccess(aBoolean);
        } else {
            return SingleResponse.buildFailure(ErrorCode.BIZ_ERROR.getCode(), "信令测试回复回调！");
        }
    }


//
//    @Operation(summary = "指令下发-批量指令下发")
//    @PostMapping("/down/cmd/setall")
//    public SingleResponse<JSONObject> sendCmdSetAll(@RequestBody ControlCmdSetBatchRequestDto request) {
//        if (!request.checkValid()) {
//            return new ResponseResult<JSONObject>().errMsg("错误的下行指令, 缺少必要参数").fail();
//        }
//        return cmdService.sendCmdSet(request);
//    }
//
//
//    @Operation(summary = "网关校时")
//    @PostMapping("/down/{productKey}/{gatewayDevId}/ntp/set")
//    public SingleResponse<Void> sendNtpSet(@PathVariable String productKey, @PathVariable String gatewayDevId) {
//        log.info("[1.1]url:/down/ntp/set, productKey={},serialNumber={}", productKey, gatewayDevId);
//        if (StringUtils.isEmpty(productKey) || StringUtils.isEmpty(gatewayDevId)) {
//            return new ResponseResult<Void>().errMsg("参数不能为空").fail();
//        }
//        return cmdService.sendNtpSet(productKey, gatewayDevId);
//    }
//
//    @Operation(summary = "指令下发-读取通讯设备静态信息")
//    @PostMapping("/down/info/call")
//    public SingleResponse sendCmdInfo(@RequestBody ControlCmdInfoCallDto request) {
//        log.info("接收下行指令cmd/info/call, {}", request);
//        if (!request.checkValid()) {
//            return SingleResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "错误的下行指令, 缺少必要参数");
//        }
//        return cmdService.sendCmdInfo(request);
//    }
//
//    @Operation(summary = "指令下发-读取通讯设备工况信息")
//    @PostMapping("/down/status/call")
//    public SingleResponse sendCmdStatus(@RequestBody ControlCmdStatusCallDto request) {
//        log.info("接收下行指令cmd/status/call, {}", request);
//        if (!request.checkValid()) {
//            return SingleResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "错误的下行指令, 缺少必要参数");
//        }
//        return cmdService.sendCmdStatus(request);
//    }
//
//    @Operation(summary = "指令下发-读取历史数据指令")
//    @PostMapping("/down/history/call")
//    public SingleResponse sendCmdHistory(@RequestBody ControlCmdHistoryCallDto request) {
//        log.info("接收下行指令cmd/history/call, {}", request);
//        if (!request.checkValid()) {
//            return SingleResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "错误的下行指令, 缺少必要参数");
//        }
//        return cmdService.sendCmdHistory(request);
//    }


//    @Operation(summary = "获取指令下发序列号Seq")
//    @GetMapping("/down/getSeqNo")
//    public ResponseResult getCmdSeq(String devId) {
//        return cmdService.getCmdSeq();
//    }

    @Operation(summary = "查询历史指令", tags = "通过deviceId查询某个设备的历史指令")
    @PostMapping("/history")
    public PageResponse queryHistoryCmdByDev(@RequestBody ControlCmdHistoryRequestVo request) {
        if (!request.checkValid()) {
            return PageResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "缺少必要参数");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10);
        }
        if (request.getPageNumber() == null || request.getPageNumber() < 1) {
            request.setPageNumber(1);
        }
        return cmdService.queryHistoryCmdByDev(deviceCmdVoConverter.fromHistoryCmd(request));
    }

    @Operation(summary = "查询指令下发响应", tags = "通过deviceId和指令seq查询指令下发ack结果")
    @PostMapping("/ack/list")
    public PageResponse queryCmdAck(@RequestBody ControlCmdAckListRequestVo request) {
        if (!request.checkValid()) {
            return PageResponse.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), "缺少必要参数");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10);
        }
        if (request.getPageNumber() == null || request.getPageNumber() < 1) {
            request.setPageNumber(1);
        }
        return cmdService.queryCmdAck(deviceCmdVoConverter.fromCmdAck(request));
    }
}
