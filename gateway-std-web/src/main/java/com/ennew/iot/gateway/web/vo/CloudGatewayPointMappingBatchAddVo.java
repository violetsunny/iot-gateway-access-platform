package com.ennew.iot.gateway.web.vo;


import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "点位映射关系批量添加对象")
@Data
public class CloudGatewayPointMappingBatchAddVo {

    @Schema(description = "映射关系对象集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pointMappingList不能为空")
    private List<CloudGatewayPointMappingAddVo> pointMappingList;


    public List<CloudGatewayPointMappingEntity> createEntityList(String gatewayCode, String bladeAuth) {
        Date now = new Date();
        String account = null;
        if (StringUtils.isNotEmpty(bladeAuth)) {
            account = JwtUtil.getInfoFromToken(bladeAuth, "account");
        }
        String username = account;
        return pointMappingList.stream()
                .map(v -> {
                    CloudGatewayPointMappingEntity entity = new CloudGatewayPointMappingEntity();
                    entity.setCreateTime(now);
                    entity.setUpdateTime(now);
                    entity.setCreateUser(username);
                    entity.setUpdateUser(username);
                    entity.setPointId(v.getPointId());
                    entity.setCloudGatewayCode(gatewayCode);
                    entity.setDeviceId(v.getDeviceId());
                    entity.setMetric(v.getMetric());
                    entity.setProductId(v.getProductId());
                    return entity;
                })
                .collect(Collectors.toList());
    }
}
