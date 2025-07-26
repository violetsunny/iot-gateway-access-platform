package com.ennew.iot.gateway.web.vo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.enums.ModelSourceEnum;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Schema(description = "云网关新增信息")
public class CloudGatewayAddVo {

    @Schema(description = "云网关名称")
    @NotNull(message = "云网关名称不能为空")
    private String cloudGatewayName;

    @Schema(description = "云网关编码")
    @NotNull(message = "云网关编码不能为空")
    private String cloudGatewayCode;

    @Schema(description = "云网关类型")
    @NotNull(message = "云网关类型不能为空")
    private Integer cloudGatewayType;

    @Schema(description = "来源")
//    @NotNull
//    @EnumValid(enumClass = ModelSourceEnum.class,message = "来源[platformSource]不在枚举内",checkMethod = "checkCode")
    private String platformSource;

    @Schema(description = "描述信息")
    private String description;

//    public TrdPlatformInfoEntity createEntity(String bladeAuth) {
//        TrdPlatformInfoEntity entity = new TrdPlatformInfoEntity();
//        entity.setPCode(this.cloudGatewayCode);
//        entity.setPType(this.cloudGatewayType);
//        entity.setPName(this.cloudGatewayName);
//        entity.setPSource(StringUtils.isNotBlank(this.platformSource)?this.platformSource: ModelSourceEnum.CUSTOM.getCode());
//        entity.setRemark(this.description);
//        Date current = new Date();
//        entity.setCreateTime(current);
//        entity.setUpdateTime(current);
//        entity.setIsDelete(0);
//        entity.setStatus(1);
//        if (StringUtils.isNotEmpty(bladeAuth)) {
//            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
//            entity.setCreateUser(account);
//            entity.setUpdateUser(account);
//        }
//        return entity;
//    }

}
