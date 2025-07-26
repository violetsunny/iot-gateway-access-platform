package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.web.validate.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 上午10:03 2023/8/17
 */
@Schema(description = "CloudDockingDataParamsVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingDataParamsVo {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空！", groups = ValidationGroups.Insert.class)
    private String id;

    @Schema(description = "prodId", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "prodId不能为空！", groups = ValidationGroups.Insert.class)
    private String prodId;

    @Schema(description = "params", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "params不能为空！", groups = ValidationGroups.Insert.class)
    List<CloudDockingParamsCmdVo> params;

}
