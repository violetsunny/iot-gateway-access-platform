package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "JS协议副本")
@Data
public class ProtocolReplicaVo {

    @Schema(description = "协议id")
    private String id;

    @Schema(description = "协议上下行[1:上行,2:下行]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer way;

    @Schema(description = "协议配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，协议配置不能为空！", groups = ValidationGroups.Insert.class)
    private Map<String, Object> configuration;

}
