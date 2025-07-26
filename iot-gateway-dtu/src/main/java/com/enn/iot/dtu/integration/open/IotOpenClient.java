package com.enn.iot.dtu.integration.open;

import com.enn.iot.dtu.common.util.http.HttpTemplate;
import com.enn.iot.dtu.integration.properties.IotIntegrationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName IotOpenClient
 * @Description
 * @Author nixiaolin
 * @Date 2022/2/15 10:35
 **/

@Component
@Slf4j
public class IotOpenClient {
    private final String DTU_CTRL_COMMAND_URI = "/dtu/cmd/query";
    private final IotIntegrationProperties iotIntegrationProperties;
    private final HttpTemplate httpTemplate;

    private IotOpenClient(IotIntegrationProperties iotIntegrationProperties, HttpTemplate httpTemplate) {
        this.iotIntegrationProperties = iotIntegrationProperties;
        this.httpTemplate = httpTemplate;
    }

    public ResControlCmdDTO loadControlCommand(String gatewaySn) throws InterruptedException {
        if (StringUtils.isEmpty(gatewaySn)) {
            log.warn("[5.1.0] 下行指令执行,网关标识为空!");
            return null;
        }
        int count = 0;
        String resultStr = "";
//        String url = String.format("%s%s?gatewaySerialNum=%s&token=%s", iotIntegrationProperties.getOpenBaseUrl(),
//            DTU_CTRL_COMMAND_URI, gatewaySn, iotIntegrationProperties.getOpenToken());
//        while (count <= Constants.MAX_RETRY_COUNT) {
//            try {
//                resultStr = httpTemplate.get(url);
//                break;
//            } catch (Exception ex) {
//                log.warn("[5.1.1] 第" + count + "次调用open服务接口/dtu/cmd/query失败，域名信息：" + url, ex);
//                Thread.sleep(Constants.RETRY_INTERVAL_TIMES);
//                ++count;
//            }
//        }
//
//        if (StringUtils.isEmpty(resultStr)) {
//            log.error("[5.1.2] 调用open服务接口获取到的数据为空");
//            return null;
//        }
//
//        return JsonUtils.readObject(resultStr, ResControlCmdDTO.class);
        return null;
    }
}
