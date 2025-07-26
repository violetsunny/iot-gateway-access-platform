package com.ennew.iot.gateway.biz.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.core.bo.HttpEventDataBo;
import com.ennew.iot.gateway.core.bo.HttpGatewayStatusBo;
import com.ennew.iot.gateway.core.bo.HttpProtocolMessageBO;

import java.util.List;
import java.util.Map;

/**
 * @Author: alec
 * Description: http接入
 * @date: 下午3:17 2023/4/27
 */
public interface HttpGatewayService {

    /**
     * 实时数据上报
     * @param pKey 产品编码
     * @param messageList 上报设备参数
     * */
    void realDataReporting(String pKey,List<HttpProtocolMessageBO> messageList);


    /**
     * 实时数据上报
     * @param pKey 产品编码
     * @param productId 产品ID
     * @param messageList 上报设备参数
     * */
    void realDataReporting(String productId, String pKey,List<HttpProtocolMessageBO> messageList);


    /**
     * 历史数据上报
     * @param pKey 产品编码
     * @param productId 产品ID
     * @param messageList 上报设备参数
     * */
    void historyDataReporting(String productId, String pKey,List<HttpProtocolMessageBO> messageList);

    /**
     * 事件上报
     * @param httpEventDataBo
     * @return
     */
    Boolean eventReport(HttpEventDataBo httpEventDataBo) throws Exception;


    /**
     * 设备信息上报
     * @param data 上报设备参数
     * */
    Boolean infoDataReporting(JSONObject data);

    /**
     * 设备工况上报
     * @param data 上报设备参数
     * */
    Boolean statusDataReporting(HttpGatewayStatusBo data);
}
