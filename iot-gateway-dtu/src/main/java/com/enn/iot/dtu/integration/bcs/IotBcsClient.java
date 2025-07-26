package com.enn.iot.dtu.integration.bcs;

import com.alibaba.fastjson.JSONObject;
import com.enn.iot.dtu.common.util.http.HttpTemplate;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.bcs.dto.ResDevicePointDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResLastTimeUpdateDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResListDeviceDTO;
import com.enn.iot.dtu.integration.bcs.dto.ResListPointInfoDTO;
import com.enn.iot.dtu.integration.constant.Constants;
import com.enn.iot.dtu.integration.properties.IotIntegrationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IotBcsClient {

    private static final String UPDATE_TIME_URI = "/dtu/lastTime";
    private static final String POINT_URI = "/dtu/point";
    private static final String DEVICE_URI = "/device/childDeviceListPage";

//    private final String BASE_TOOL_URL = "https://iot-delivery-tool.fat.ennew.com";
//    private final String BASE_DEVICE_URL = " http://10.39.64.22:9999";
    private static final String DEVICE_POINT_URI = "/sdk/v1/device/measureInfo";

    @Value("${ennew.iot.device.url}")
    private String BASE_DEVICE_URL;

    @Value("${ennew.iot.delivery.url}")
    private String BASE_TOOL_URL;

    @Value("${ennew.iot.delivery.accessKey}")
    private String BASE_TOOL_ACCESSKEY;
    @Value("${ennew.iot.delivery.appKey:iot-gateway-other}")
    private String APP_KEY;

    private final IotIntegrationProperties iotIntegrationProperties;
    private final HttpTemplate httpTemplate;

    private IotBcsClient(IotIntegrationProperties iotIntegrationProperties, HttpTemplate httpTemplate) {
        this.iotIntegrationProperties = iotIntegrationProperties;
        this.httpTemplate = httpTemplate;
    }


    /**
     * @param gatewaySn
     * @return if null, 接口调用失败
     */
    public ResLastTimeUpdateDTO queryMainDataUpdateTimeByGatewaySn(String gatewaySn) throws InterruptedException {
        int count = 1;
        String resultStr = "";
        String url = String.format("%s%s?token=%s&gatewaySerialNum=%s", iotIntegrationProperties.getBcsBaseUrl(),
                UPDATE_TIME_URI, iotIntegrationProperties.getBcsToken(), gatewaySn);
        while (count <= Constants.MAX_RETRY_COUNT) {
            try {
                resultStr = httpTemplate.get(url);
                break;
            } catch (Exception ex) {
                log.warn("第" + count + "次调用bcs服务接口/dtu/lastTime失败，域名信息：" + url, ex);
                Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
                ++count;
            }
        }

        if (StringUtils.isEmpty(resultStr)) {
            log.error("调用bcs服务接口获取到的数据为空");
            return null;
        }

        return JsonUtils.readObject(resultStr, ResLastTimeUpdateDTO.class);
    }

    public ResListPointInfoDTO queryPointListByGatewaySn(String gatewaySn) throws InterruptedException {
        int count = 1;
        String resultStr = "";
        String url = String.format("%s%s?token=%s&gatewaySerialNum=%s", iotIntegrationProperties.getBcsBaseUrl(),
                POINT_URI, iotIntegrationProperties.getBcsToken(), gatewaySn);
        while (count <= Constants.MAX_RETRY_COUNT) {
            try {
                resultStr = httpTemplate.get(url);
                break;
            } catch (Exception ex) {
                log.warn("第" + count + "次调用bcs服务接口/dtu/point失败，域名信息：" + url, ex);
                Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
                ++count;
            }
        }

        if (StringUtils.isEmpty(resultStr)) {
            log.error("调用bcs服务接口获取到的数据为空");
            return null;
        }

        return JsonUtils.readObject(resultStr, ResListPointInfoDTO.class);
    }


    /**
     * 根据网关id获取网关下所有设备列表
     *
     * @param gatewaySn
     * @return
     * @throws InterruptedException
     */

    public List<ResListDeviceDTO.DeviceInfoDTO> queryDeviceListByGatewaySn(String gatewaySn) throws InterruptedException {


        String resultStr = "";
        String url = BASE_DEVICE_URL + DEVICE_URI;
        String body = "{\"current\":1,\"size\":100,\"parentId\":\"\",\"sn\":\"\",\"name\":\"\"}";
        JSONObject req = JSONObject.parseObject(body);
        req.put("parentId", gatewaySn);

        int pageIndex = 1;
        int totalRes = 0;
        int total = 1;


        List<ResListDeviceDTO.DeviceInfoDTO> resultList = new ArrayList<>();

        while (totalRes < total) {
            req.put("current", pageIndex);
            int count = 1;
            while (count <= Constants.MAX_RETRY_COUNT) {
                try {
                    resultStr = httpTemplate.postJson(url, req);
                    break;
                } catch (Exception ex) {
                    log.warn("第" + count + "次调用bcs服务接口/dtu/point失败，域名信息：" + url, ex);
                    Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
                    ++count;
                }
            }
            if (StringUtils.isEmpty(resultStr)) {
                log.error("调用bcs服务接口获取到的数据为空");
                return null;
            }

            ResListDeviceDTO resListDeviceDTO = JsonUtils.readObject(resultStr, ResListDeviceDTO.class);
            if (!resListDeviceDTO.isSuccess()) {
                log.error("调用bcs服务接口获取到的数据失败，info：{}", resultStr);
            }
            ResListDeviceDTO.Data data = resListDeviceDTO.getData();

            resultList.addAll(data.getList());
            totalRes = resultList.size();
            total = data.getTotal();

            pageIndex++;
        }


        return resultList;
    }

    /**
     * 根据设备id获取点位信息列表
     *
     * @param deviceId
     * @return
     * @throws InterruptedException
     */
    public ResDevicePointDTO queryPointListByDeviceId(String deviceId) throws InterruptedException {

        String resultStr = "";
        String url = BASE_TOOL_URL + DEVICE_POINT_URI;
        JSONObject req = new JSONObject();
        req.put("deviceId", deviceId);
        req.put("accessName","");

        Map<String,String> header =new HashMap<>();
        header.put("X-GW-AccessKey",BASE_TOOL_ACCESSKEY);
        header.put("app-key",APP_KEY);
        int count = 1;
        while (count <= Constants.MAX_RETRY_COUNT) {
            try {
                resultStr = httpTemplate.postJson(url, req,header);
                break;
            } catch (Exception ex) {
                log.warn("第" + count + "次调用bcs服务接口/dtu/point失败，域名信息：" + url, ex);
                Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
                ++count;
            }
        }
        if (StringUtils.isEmpty(resultStr)) {
            log.error("调用bcs服务接口获取到的数据为空");
            return null;
        }
        return JsonUtils.readObject(resultStr, ResDevicePointDTO.class);
    }
}
