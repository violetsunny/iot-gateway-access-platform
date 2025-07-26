package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 上午10:02 2023/4/19
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "数据上报报文")
public class HttpGatewayRtgDataCmd {

    @Schema(description = "sysId")
    private String sysId;

    @Schema(description = "设备编号")
    @NotNull(message = "dev is not null")
    private String dev;

    @Schema(description = "时间戳")
    @NotNull(message = "ts is not null")
    private Long ts;

    @Schema(description = "上报报文")
    @NotEmpty(message = "d is not null")
    @Valid
    private List<RtgData> d;

    @Schema(description = "Y-续传，N-非续传")
    private String resume = "N"; //Y-续传，N-非续传

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class RtgData {

        @Schema(description = "测点")
        @NotNull(message = "m is not null")
        private String m;

        @Schema(description = "测点值")
        @NotNull(message = "v is not null")
        private Object v;

    }

}
