package com.enn.iot.dtu.integration.cit;

import com.enn.iot.dtu.common.util.http.HttpTemplate;
import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.enn.iot.dtu.integration.cit.dto.ResGatewaySerialNumDTO;
import com.enn.iot.dtu.integration.constant.Constants;
import com.enn.iot.dtu.integration.properties.IotIntegrationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class IotCitClient {

    private static final String GATEWAY_REGISTER_PACKAGE_URI = "/gateway/registerPackage/";

    private final IotIntegrationProperties iotIntegrationProperties;
    private final HttpTemplate httpTemplate;

    public ResGatewaySerialNumDTO getGatewaySnByRegisterPackage(String registerPackage) throws InterruptedException {
        if (!registerPackage.matches(Constants.GATEWAY_SN_PATTERN)) {
            log.warn("[1.2.1] 注册包内容不符合规范，注册包内容：{}，即将断开连接", registerPackage);
            return null;
        }
        int count = 0;
        String resultStr = "";
        String url = String.format("%s%s%s?token=%s", iotIntegrationProperties.getCitBaseUrl(),
                GATEWAY_REGISTER_PACKAGE_URI, registerPackage, iotIntegrationProperties.getCitToken());
        while (count <= Constants.MAX_RETRY_COUNT) {
            try {
                resultStr = httpTemplate.get(url);
                break;
            } catch (Exception ex) {
                log.warn("第" + count + "次调用cit服务接口/gateway/registerPackage失败，域名信息：" + url, ex);
                Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
                ++count;
            }
        }

        if (StringUtils.isEmpty(resultStr)) {
            log.error("调用cit服务接口获取到的数据为空");
            return null;
        }

        return JsonUtils.readObject(resultStr, ResGatewaySerialNumDTO.class);
    }
}
