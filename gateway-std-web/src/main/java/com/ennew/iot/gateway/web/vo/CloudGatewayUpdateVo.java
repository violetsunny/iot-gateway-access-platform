package com.ennew.iot.gateway.web.vo;


import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Schema(description = "云网关编辑对象")
public class CloudGatewayUpdateVo {


    @Schema(description = "云网关名称")
    @NotNull(message = "云网关名称不能为空")
    private String cloudGatewayName;

    @Schema(description = "描述信息")
    private String remark;



    public TrdPlatformInfoEntity createEntity(Long id, String bladeAuth) {
        TrdPlatformInfoEntity entity = new TrdPlatformInfoEntity();
        entity.setId(id);
        entity.setPName(this.cloudGatewayName);
        entity.setRemark(this.remark);
        Date current = new Date();
        entity.setUpdateTime(current);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setUpdateUser(account);
        }
        return entity;
    }
}
