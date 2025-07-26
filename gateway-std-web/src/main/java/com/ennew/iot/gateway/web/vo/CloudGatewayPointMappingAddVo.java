package com.ennew.iot.gateway.web.vo;


import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Schema(description = "点位映射关系添加对象")
@Data
public class CloudGatewayPointMappingAddVo {


    @Schema(description = "测点ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "测点ID不能为空")
    private Long pointId;


    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "设备ID不能为空")
    private String deviceId;


    @Schema(description = "测点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "测点不能为空")
    private String metric;


    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品ID不能为空")
    private String productId;


    public CloudGatewayPointMappingEntity createEntity(String gatewayCode, String bladeAuth){
        CloudGatewayPointMappingEntity entity = new CloudGatewayPointMappingEntity();
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setCreateUser(account);
            entity.setUpdateUser(account);
        }
        entity.setPointId(this.pointId);
        entity.setCloudGatewayCode(gatewayCode);
        entity.setDeviceId(this.deviceId);
        entity.setMetric(this.metric);
        entity.setProductId(this.productId);
        return entity;
    }

}
