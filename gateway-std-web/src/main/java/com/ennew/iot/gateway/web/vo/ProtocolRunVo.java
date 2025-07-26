package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Map;

@Schema(description = "ProtocolRunVo对象")
@Data
public class ProtocolRunVo {

    @Schema(description = "协议id")
    @NotBlank(message = "数据验证失败,协议id不能为空！")
    private String id;

    @Schema(description = "输入消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败,输入消息不能为空！")
    private String inputMessage;


}
