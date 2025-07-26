package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: alec
 * Description: http 接入参数
 * @date: 上午9:40 2023/4/19
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "数据上报")
public class HttpGatewayRtgCmd {

    @NotNull(message = "ver is not null")
    @Schema(description = "版本")
    private String ver;

    @Schema(description = "pKey")
    private String pKey;

    @Schema(description = "设备序列号")
    private String sn;

    @Schema(description = "时间戳")
    @NotNull(message = "ts is not null")
    private Long ts;

    @NotEmpty(message = "devs is not null")
    @Schema(description = "报文")
    @Valid
    private List<HttpGatewayRtgDataCmd> devs;

}
