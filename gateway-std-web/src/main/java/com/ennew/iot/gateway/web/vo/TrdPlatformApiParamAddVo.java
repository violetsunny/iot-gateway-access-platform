package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "Api参数信息")
public class TrdPlatformApiParamAddVo implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long apiId;

    @Schema(description = "参数Key")
    @NotNull(message = "参数Key不能为空")
    private String paramKey;

    @Schema(description = "参数类型")
    @NotNull(message = "参数类型不能为空")
    private Integer paramType;

    @Schema(description = "参数位置")
    @NotNull(message = "参数位置不能为空")
    private Integer paramPosition;

    @Schema(description = "参数值")
    @NotNull(message = "参数值不能为空")
    private String paramValue;

    @Schema(description = "备注")
    private String remark;

}
