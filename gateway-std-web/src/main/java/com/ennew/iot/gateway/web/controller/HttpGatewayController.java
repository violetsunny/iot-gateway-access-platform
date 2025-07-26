package com.ennew.iot.gateway.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.biz.gateway.service.HttpGatewayService;
import com.ennew.iot.gateway.core.bo.HttpEventDataBo;
import com.ennew.iot.gateway.core.bo.HttpGatewayStatusBo;
import com.ennew.iot.gateway.core.bo.HttpProtocolMessageBO;
import com.ennew.iot.gateway.web.converter.HttpProtocolMessageVoConverter;
import com.ennew.iot.gateway.web.vo.HttpEventDataCmd;
import com.ennew.iot.gateway.web.vo.HttpGatewayRtgCmd;
import com.ennew.iot.gateway.web.vo.HttpGatewayRtgDataCmd;
import com.ennew.iot.gateway.web.vo.HttpGatewayStatusCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.validator.BaseAssert;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:26 2023/4/19
 */
@RestController
@RequestMapping("/std")
@Tag(name = "HTTP网关")
@Slf4j
//@CatchAndLog
@RequiredArgsConstructor
public class HttpGatewayController {

    private final HttpGatewayService httpGatewayService;

    private final HttpProtocolMessageVoConverter messageVoConverter;

    /**
     * HTTP标准协议上报
     **/
    @PostMapping("/rtg")
    @Operation(summary = "HTTP 标准协议上报")
    public SingleResponse<String> stdRtg(@Validated @RequestBody HttpGatewayRtgCmd httpGatewayRtgCmd) {
        log.info("http gateway std  body: {}", httpGatewayRtgCmd);
        List<HttpGatewayRtgDataCmd> rtgDataCmd = httpGatewayRtgCmd.getDevs();
        List<HttpProtocolMessageBO> messageList =  messageVoConverter.toMessageBodyBO(rtgDataCmd);
        httpGatewayService.realDataReporting(httpGatewayRtgCmd.getPKey(),messageList);
        return SingleResponse.buildSuccess(httpGatewayRtgCmd.getSn());
    }

    /**
     * HTTP非标准协议上报
     **/
    @PostMapping("/{productId}/rtg")
    @Operation(summary = "HTTP 非标准协议上报")
    public SingleResponse productRtg(@PathVariable(value = "productId")String productId, @RequestBody HttpGatewayRtgCmd httpGatewayRtgCmd) {
        log.info("http gateway std productId:{}  body: {}",productId, httpGatewayRtgCmd);
        List<HttpGatewayRtgDataCmd> rtgDataCmd = httpGatewayRtgCmd.getDevs();
        List<HttpProtocolMessageBO> messageList =  messageVoConverter.toMessageBodyBO(rtgDataCmd);
        httpGatewayService.realDataReporting(productId,httpGatewayRtgCmd.getPKey(),messageList);
        return SingleResponse.buildSuccess();
    }

    /**
     * 历史数据上报
     **/
    @PostMapping("{productId}/history")
    @Operation(summary = "HTTP 历史数据上报")
    public SingleResponse historyStd(@PathVariable(value = "productId") String productId,
                                     @Validated @RequestBody HttpGatewayRtgCmd httpGatewayRtgCmd) {
        log.info("http gateway std productId: {} - body: {}", productId, httpGatewayRtgCmd);
        List<HttpGatewayRtgDataCmd> rtgDataCmd = httpGatewayRtgCmd.getDevs();
        List<HttpProtocolMessageBO> messageList =  messageVoConverter.toMessageBodyBO(rtgDataCmd);
        httpGatewayService.historyDataReporting(productId,httpGatewayRtgCmd.getPKey(),messageList);
        return SingleResponse.buildSuccess();
    }

    /**
     * 设备信息上报
     **/
    @PostMapping("/info")
    @Operation(summary = "设备信息上报")
    public SingleResponse<Boolean> infoStd(@RequestBody JSONObject httpGatewayInfoData) {
        //info上报设备信息，仅对原始报文进行转发 原始报文格式不固定
        log.info("http gateway info data body: {}", httpGatewayInfoData);
        BaseAssert.isBlank(httpGatewayInfoData.getString("pKey"), ErrorCode.PARAMETER_ERROR,"pKey不能为空");
        BaseAssert.isBlank(httpGatewayInfoData.getString("sn"), ErrorCode.PARAMETER_ERROR,"sn不能为空");
        BaseAssert.isNull(httpGatewayInfoData.getLong("ts"), ErrorCode.PARAMETER_ERROR.getCode(),"ts不能为空");
        return SingleResponse.buildSuccess(httpGatewayService.infoDataReporting(httpGatewayInfoData));
    }

    /**
     * 设备工况上报
     **/
    @PostMapping("/status")
    @Operation(summary = "设备工况上报")
    public SingleResponse<Boolean> statusStd(@Validated @RequestBody HttpGatewayStatusCmd httpGatewayStatusCmd) {
        log.info("http gateway status - body: {}", httpGatewayStatusCmd);
        HttpGatewayStatusBo httpGatewayStatusBo =  messageVoConverter.toStatusRequestBO(httpGatewayStatusCmd);
        return SingleResponse.buildSuccess(httpGatewayService.statusDataReporting(httpGatewayStatusBo));
    }

    @PostMapping("/event")
    @Operation(summary = "事件上报")
    public SingleResponse<Boolean> event(@Validated @RequestBody HttpEventDataCmd httpEventDataCmd) throws Exception {
        log.info("http event report  body: {}", JSON.toJSONString(httpEventDataCmd));
        HttpEventDataBo httpEventDataBo =  messageVoConverter.toEventRequestBO(httpEventDataCmd);
        return SingleResponse.buildSuccess(httpGatewayService.eventReport(httpEventDataBo));
    }

}
