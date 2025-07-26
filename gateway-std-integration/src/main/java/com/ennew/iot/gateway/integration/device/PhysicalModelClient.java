package com.ennew.iot.gateway.integration.device;

import com.alibaba.fastjson.JSONObject;
import com.ennew.iot.gateway.integration.device.model.req.IotDevPhysicalModelMeasureQueryReq;
import com.ennew.iot.gateway.integration.device.model.req.IotDevPhysicalModelMqttAuthReq;
import com.ennew.iot.gateway.integration.device.model.resp.IotDevPhysicalModelIdResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 查询物模型的接口
 *
 * @author ruanhong
 */
@FeignClient(url = "${ennew.iot.device.url}", name = "iot-device-cmd")
public interface PhysicalModelClient {

    String HEADER_X_GW_ACCESS_KEY = "x-gw-accesskey=jJjjV9gxqh0ijVUclPwm5mNiSZZdmukc";

//    String HEADER_ACCESS_CODE = "accessCode=II7421Hg08Oj9yd91e";
    String HEADER_ACCESS_CODE = "accessCode=4I5vxoyNoXdk82JlDD";

    /**
     * 2.12.1 校验账号是否存在
     * https://console-docs.apipost.cn/preview/222304ac6853c4a5/930aaed5e2d02249?target_id=b8e59884-dec4-4119-9342-f07d9191a980#%E6%88%90%E5%8A%9F%EF%BC%88200%EF%BC%89
     *
     * @param ro
     * @return
     */
    @PostMapping(value = "/device/checkAccountByClientId",headers = {HEADER_X_GW_ACCESS_KEY,HEADER_ACCESS_CODE})
    IotDevPhysicalModelIdResp<Object> checkAccountByClientId(@RequestBody IotDevPhysicalModelMqttAuthReq ro);

    /**
     * 2.1.1 根据设备ID查询设备详情
     * hhttps://console-docs.apipost.cn/preview/222304ac6853c4a5/930aaed5e2d02249?target_id=9148fe0d-4d62-4f06-f51e-6d1ae34d8263
     *
     * @param deviceId
     * @return
     */
    @GetMapping(value = "/device/get/{deviceId}",headers = {HEADER_X_GW_ACCESS_KEY,HEADER_ACCESS_CODE})
    IotDevPhysicalModelIdResp<JSONObject> getDeviceById(@PathVariable(value = "deviceId") String deviceId);

    /**
     * 2.5.1 根据设备ID获取量测属性定义
     * https://console-docs.apipost.cn/preview/222304ac6853c4a5/930aaed5e2d02249?target_id=a56913fa-9544-40a0-ac42-08951111f8b5
     *
     * @param ro
     * @return
     */
    @PostMapping(value ="/device/measureDefinitionListPage",headers = {HEADER_X_GW_ACCESS_KEY,HEADER_ACCESS_CODE})
    IotDevPhysicalModelIdResp<JSONObject> getMeasureDefinition(@RequestBody IotDevPhysicalModelMeasureQueryReq ro);

}
