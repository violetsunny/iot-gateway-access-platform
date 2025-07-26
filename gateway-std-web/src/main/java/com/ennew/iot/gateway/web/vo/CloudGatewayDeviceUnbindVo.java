package com.ennew.iot.gateway.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "云网关设备解绑请求")
public class CloudGatewayDeviceUnbindVo {

    @Schema(description = "设备列表")
    @NotNull
    private List<String> deviceIdList;
}
