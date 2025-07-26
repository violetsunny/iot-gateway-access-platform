package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Schema(description = "云网关设备绑定请求")
public class CloudGatewayDeviceBindVo {

    @Schema(description = "设备列表")
    @NotNull
    private List<String> deviceIdList;





    public List<CloudGatewayDeviceEntity> createEntityList(String gatewayCode, String bladeAuth){
        Date now = new Date();
        String account = null;
        if (StringUtils.isNotEmpty(bladeAuth)) {
            account = JwtUtil.getInfoFromToken(bladeAuth, "account");
        }
        String userName = account;
        return deviceIdList.stream().map(dev -> {
            CloudGatewayDeviceEntity entity = new CloudGatewayDeviceEntity();
            entity.setDeviceId(dev);
            entity.setCreateTime(now);
            entity.setCreateUser(userName);
            entity.setUpdateTime(now);
            entity.setUpdateUser(userName);
            entity.setCloudGatewayCode(gatewayCode);
            return entity;
        }).collect(Collectors.toList());
    }

}
