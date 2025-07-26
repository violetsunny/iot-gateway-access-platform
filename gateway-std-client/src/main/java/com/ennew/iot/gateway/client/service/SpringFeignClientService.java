package com.ennew.iot.gateway.client.service;

import com.ennew.iot.gateway.client.dto.HelloFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.kdla.framework.dto.SingleResponse;

/**
 * 使用FeignClient作为客户端扫描该类后，可直接使用该客户端进行服务调用
 * 新版本建议直接使用 dubbo调用
 * name 属性为 注册中心 服务名，请确认大小写；（一般默认Eureka上为大写）
 */
@FeignClient(name="gateway-std",contextId = "SpringFeignClientService")
public interface SpringFeignClientService {

    @RequestMapping(value = "/body/hello",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    SingleResponse<HelloFeignDto> helloFeign(HelloFeignDto helloFeignDto);

}
