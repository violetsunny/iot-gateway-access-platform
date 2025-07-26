package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.common.constants.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "Modbus网关配置")
public class CloudGatewayModbusConfigVo {



    @Schema(description = "主机IP地址")
    @NotNull(message = "主机IP不能为空")
    @Pattern(regexp = RegexConstant.IP_AND_HOSTNAME_PATTERN, message = RegexConstant.IP_AND_HOSTNAME_PATTERN_MESSAGE)
    private String host;

    @Schema(description = "端口")
    @NotNull(message = "端口不能为空")
    @Max(value = 65535, message = "请输入正确的端口")
    @Min(value = 0, message = "请输入正确的端口")
    private Integer port;

    @Schema(description = "从站地址")
    @NotNull(message = "从站地址不能为空")
    private Integer salveAddress;
}
