package com.ennew.iot.gateway.web.vo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.enums.ModelSourceEnum;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import top.kdla.framework.validator.annotation.EnumValid;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "三方云平台信息")
public class TrdPlatformInfoAddVo implements Serializable {

    /**
     * 平台类别
     */
    @Schema(description = "平台类别")
    @NotNull
    private Integer platformType;

    /**
     * 平台code
     */
    @Schema(description = "平台code")
    @NotNull
    private String platformCode;

    /**
     * 平台名字
     */
    @Schema(description = "平台名字")
    @NotNull
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String platformName;

    @Schema(description = "来源")
//    @NotNull
//    @EnumValid(enumClass = ModelSourceEnum.class,message = "来源[platformSource]不在枚举内",checkMethod = "checkCode")
    private String platformSource;

    /**
     * 配置参数json(appkey、域名等)
     */
    @Schema(description = "json化配置，根据platformType：" +
            "1 -- {\"baseUrl\":\"\",\"appKey\":\"\",\"appSecret\":\"\"} " +
            "2 -- {\"baseUrl\":\"\"}" +
            "3 -- {\"host\":\"\",\"port\":0,\"salveAddress\":0}")
    private String configJson;

    /**
     * 协议Id
     */
    @Schema(description = "协议Id")
    private String protocolId;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String remark;

    public TrdPlatformInfoEntity createEntity(TrdPlatformInfoAddVo trdPlatformInfoAddVo, String bladeAuth) {
        TrdPlatformInfoEntity entity = BeanUtil.copyProperties(trdPlatformInfoAddVo, TrdPlatformInfoEntity.class);
        Date current = new Date();
        entity.setPType(trdPlatformInfoAddVo.getPlatformType());
        entity.setPCode(trdPlatformInfoAddVo.getPlatformCode());
        entity.setPName(trdPlatformInfoAddVo.getPlatformName());
        entity.setPSource(StringUtils.isNotBlank(trdPlatformInfoAddVo.getPlatformSource())?trdPlatformInfoAddVo.getPlatformSource():ModelSourceEnum.CUSTOM.getCode());
        entity.setCreateTime(current);
        entity.setUpdateTime(current);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setCreateUser(account);
            entity.setUpdateUser(account);
        }
        entity.setStatus(1);
        entity.setIsDelete(0);
        return entity;
    }

    public TrdPlatformInfoEntity updateEntity(Long id, TrdPlatformInfoAddVo trdPlatformInfoAddVo, String bladeAuth) {
        TrdPlatformInfoEntity entity = BeanUtil.copyProperties(trdPlatformInfoAddVo, TrdPlatformInfoEntity.class);
        entity.setId(id);
        entity.setPType(trdPlatformInfoAddVo.getPlatformType());
        entity.setPCode(trdPlatformInfoAddVo.getPlatformCode());
        entity.setPName(trdPlatformInfoAddVo.getPlatformName());
        entity.setPSource(StringUtils.isNotBlank(trdPlatformInfoAddVo.getPlatformSource())?trdPlatformInfoAddVo.getPlatformSource():ModelSourceEnum.CUSTOM.getCode());
        Date current = new Date();
        entity.setUpdateTime(current);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setUpdateUser(account);
        }
        return entity;
    }

}
