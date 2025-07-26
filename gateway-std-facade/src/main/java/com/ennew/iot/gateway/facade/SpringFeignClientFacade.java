package com.ennew.iot.gateway.facade;

import com.ennew.iot.gateway.client.dto.HelloFeignDto;
import com.ennew.iot.gateway.client.service.SpringFeignClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.SingleResponse;

/**
 *
 */
@RestController("/")
@Tag(name = "hello feign")
public class SpringFeignClientFacade implements SpringFeignClientService {

    Logger logger= LoggerFactory.getLogger(SpringFeignClientFacade.class);

    @Override
    @PostMapping("body/hello")
    @Operation(summary = "feign demo")
    public SingleResponse<HelloFeignDto> helloFeign(@RequestBody HelloFeignDto helloFeignDto) {
        logger.info("hello feign, data: {}", helloFeignDto);
        helloFeignDto.setResData(
                helloFeignDto.getSrcData());

        return SingleResponse.buildSuccess(helloFeignDto);
    }

}
