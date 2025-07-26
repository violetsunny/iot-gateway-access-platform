package com.ennew.iot.gateway.biz.ctwing;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.AepDeviceManagementClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.ctg.ag.sdk.biz.aep_device_management.CreateDeviceRequest;
import com.ctg.ag.sdk.biz.aep_device_management.CreateDeviceResponse;
import com.ctg.ag.sdk.biz.aep_device_management.DeleteDeviceRequest;
import com.ctg.ag.sdk.biz.aep_device_management.DeleteDeviceResponse;
import com.ctg.ag.sdk.core.model.BaseApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.Response;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import java.util.Arrays;


/**
 * CTWing SDK 操作组件
 */
public class CtwingSDKOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtwingSDKOperator.class);
    private static final String DEFAULT_OPERATOR = "ENNEW";

    private static final String AEP_RESPONSE_SUCCESS_CODE = "0";

    private final String appKey;

    private final String appSecret;


    public CtwingSDKOperator(String appKey, String appSecret) {
        Assert.notBlank(appKey, "CTWing appKey is blank");
        Assert.notBlank(appSecret, "CTWing appSecret is blank");
        this.appKey = appKey;
        this.appSecret = appSecret;
    }


    /**
     * 指令下发
     *
     * @param masterApiKey   CTWing masterAPIKey
     * @param productId      CTWing产品ID
     * @param deviceId       CTWing设备ID
     * @param commandContent 指令内容
     * @param ttl            设备指令缓存时长，选填。单位：秒，取值范围：0-864000。 不携带则默认值：7200
     * @param dataType       数据类型：1字符串，2十六进制
     * @param operator       操作人
     * @return 请求结果
     */
    public Response sendDeviceCommand(String masterApiKey,
                                      String productId,
                                      String deviceId,
                                      String commandContent,
                                      Integer ttl,
                                      Integer dataType,
                                      String operator) {
        Assert.notBlank(masterApiKey, () -> new BizException(ErrorCode.BAD_REQUEST, "masterApiKey is null or blank"));
        Assert.notBlank(productId, () -> new BizException(ErrorCode.BAD_REQUEST, "productId is null or blank"));
        JSONObject data = JSONObject.of(
                "deviceId", deviceId,
                "productId", productId,
                "operator", getOperator(operator),
                "ttl", ttl == null ? 7200 : ttl,
                "content", JSONObject.of(
                        "payload", commandContent,
                        "dataType", dataType
                )
        );
        CreateCommandRequest commandRequest = new CreateCommandRequest();
        commandRequest.setBody(data.toString().getBytes());
        commandRequest.setParamMasterKey(masterApiKey);
        AepDeviceCommandClient aepDeviceCommandClient = null;
        try {
            aepDeviceCommandClient = AepDeviceCommandClient.newClient()
                    .appKey(appKey)
                    .appSecret(appSecret)
                    .build();
            CreateCommandResponse createCommandResponse = aepDeviceCommandClient.CreateCommand(commandRequest);
            Response response = toResponse(createCommandResponse);
            if (response.isSuccess()) {
                LOGGER.info("CTWing指令下发成功，{}", data);
            } else {
                LOGGER.info("CTWing指令下发失败，code={}，msg={}", response.getCode(), response.getMsg());
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("CTWing指令下发异常", e);
            throw new BizException(ErrorCode.BIZ_ERROR, "CTWing指令下发异常:" + e.getMessage());
        } finally {
            if (aepDeviceCommandClient != null) {
                aepDeviceCommandClient.shutdown();
            }
        }
    }


    /**
     * 设备注册
     *
     * @param masterApiKey CTWing masterAPIKey
     * @param productId    CTWing产品ID
     * @param imei         imei号
     * @param deviceName   设备名称
     * @param operator     操作人
     * @return 请求结果
     */
    public Response deviceRegister(String masterApiKey, String productId, String imei, String deviceName, String operator) {
        Assert.notBlank(masterApiKey, () -> new BizException(ErrorCode.BAD_REQUEST, "masterApiKey is null or blank"));
        Assert.notBlank(productId, () -> new BizException(ErrorCode.BAD_REQUEST, "productId is null or blank"));
        Assert.notBlank(imei, () -> new BizException(ErrorCode.BAD_REQUEST, "imei is null or blank"));
        Assert.notBlank(deviceName, () -> new BizException(ErrorCode.BAD_REQUEST, "deviceName is null or blank"));
        JSONObject data = JSONObject.of(
                "deviceName", deviceName,
                "imei", imei,
                "operator", getOperator(operator),
                "productId", productId,
                "other", JSONObject.of("autoObserver", 0)
        );
        CreateDeviceRequest createDeviceRequest = new CreateDeviceRequest();
        createDeviceRequest.setParamMasterKey(masterApiKey);
        createDeviceRequest.setBody(data.toString().getBytes());
        AepDeviceManagementClient aepDeviceManagementClient = null;
        try {
            aepDeviceManagementClient = AepDeviceManagementClient.newClient()
                    .appKey(appKey)
                    .appSecret(appSecret)
                    .build();
            CreateDeviceResponse createDeviceResponse = aepDeviceManagementClient.CreateDevice(createDeviceRequest);
            Response response = toResponse(createDeviceResponse);
            if (response.isSuccess()) {
                LOGGER.info("CTWing设备注册成功， {}", data);
            } else {
                LOGGER.error("CTWing设备注册失败，code={}, msg={}", response.getCode(), response.getMsg());
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("CTWing设备注册异常", e);
            throw new BizException(ErrorCode.BIZ_ERROR, "CTWing设备注册异常:" + e.getMessage());
        } finally {
            if (aepDeviceManagementClient != null) {
                aepDeviceManagementClient.shutdown();
            }
        }
    }


    /**
     * 设备删除
     *
     * @param masterApiKey CTWing masterAPIKey
     * @param productId    CTWing产品ID
     * @param deviceIds    设备ID数组
     * @return 请求结果
     */
    public Response deviceDelete(String masterApiKey, String productId, String... deviceIds) {
        DeleteDeviceRequest deleteDeviceRequest = new DeleteDeviceRequest();
        deleteDeviceRequest.setParamMasterKey(masterApiKey);
        deleteDeviceRequest.setParamProductId(productId);
        deleteDeviceRequest.setParamDeviceIds(deviceIds);
        AepDeviceManagementClient aepDeviceManagementClient = null;
        try {
            aepDeviceManagementClient = AepDeviceManagementClient.newClient()
                    .appKey(appKey)
                    .appSecret(appSecret)
                    .build();
            DeleteDeviceResponse deleteDeviceResponse = aepDeviceManagementClient.DeleteDevice(deleteDeviceRequest);
            Response response = toResponse(deleteDeviceResponse);
            if (response.isSuccess()) {
                LOGGER.info("CTWing设备删除成功，{}", Arrays.toString(deviceIds));
            } else {
                LOGGER.info("CTWing设备删除失败，code={}, msg={}", response.getCode(), response.getMsg());
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("CTWing设备删除异常", e);
            throw new BizException(ErrorCode.BIZ_ERROR, "CTWing设备删除异常:" + e.getMessage());
        } finally {
            if (aepDeviceManagementClient != null) {
                aepDeviceManagementClient.shutdown();
            }
        }
    }


    /**
     * 转换CTWing接口响应内容
     *
     * @param apiResponse CTWing接口返回结果
     * @return ENN请求结果
     */
    private Response toResponse(BaseApiResponse apiResponse) {
        LOGGER.debug("CTWing response: {}", apiResponse);
        if (apiResponse == null) {
            return Response.buildFailure(ErrorCode.UNKNOWN_ERROR);
        }
        JSONObject responseBody = JSON.parseObject(apiResponse.getBody());
        if (responseBody == null) {
            return Response.buildFailure(ErrorCode.FAIL.getCode(), "无响应内容");
        }
        String code = responseBody.getString("code");
        String msg = responseBody.getString("msg");
        if (apiResponse.getStatusCode() != 200 || !AEP_RESPONSE_SUCCESS_CODE.equals(code)) {
            return Response.buildFailure(code, msg);
        }
        return Response.buildSuccess();
    }


    /**
     * 获取操作人，默认为 ENNEW
     *
     * @param operator 操作人名称
     * @return 操作人名称
     */
    private String getOperator(String operator) {
        return StringUtils.hasText(operator) ? operator : DEFAULT_OPERATOR;
    }


    /*
    public static void main(String[] args) {
        CtwingSDKOperator ctwingSDKOperator = new CtwingSDKOperator("G1P0bqA1lv1", "Drkg25ehhb");
        ctwingSDKOperator.sendDeviceCommand(
                "9e86aa2f73384a74b1653d74734871c4",
                "16996520",
                "5abdcf285d4e462596853fa2340dd545",
                "83401501621521302023020107100062085af1db0a54c5d8b8bc0f3430c7fe4d1aed",
                null,
                2,
                null);
    }*/
}
