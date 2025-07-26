package com.ennew.iot.gateway.web.vo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
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
@Schema(description = "三方云平台模型映射新增信息")
public class TrdPlatformModelRefAddVo implements Serializable {

    @Schema(description = "平台编码")
    @NotNull
    private String platformCode;

    @Schema(description = "恩牛模型来源")
    @NotNull
    @EnumValid(enumClass = ModelSourceEnum.class,message = "来源[ennModelSource]不在枚举内",checkMethod = "checkCode")
    private String ennModelSource;

    @Schema(description = "恩牛模型Id")
    @NotNull
    private String ennModelId;

    @Schema(description = "恩牛模型名称")
    @NotNull
    private String ennModelName;

    @Schema(description = "恩牛模型标识")
    @NotNull
    private String ennModelCode;

    @Schema(description = "产品ID")
    @NotNull
    private String ennProductId;

    @Schema(description = "恩牛产品名称")
    @NotNull
    private String ennProductName;

    @Schema(description = "平台模型名称")
    @NotNull
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = "平台模型名称 " + RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String platformModelName;

    @Schema(description = "平台模型编码")
    @NotNull
    @Pattern(regexp = RegexConstant.CODE_PATTER, message = "平台模型编码 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    private String platformModelCode;

    @Schema(description = "平台品牌名称")
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = "平台品牌名称 " + RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String platformBrand;

    @Schema(description = "平台型号")
    @Pattern(regexp = RegexConstant.CODE_PATTER, message = "平台型号 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    private String platformSpec;

    @Schema(description = "备注")
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = "备注 " + RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String remark;

    public TrdPlatformModelRefEntity createEntity(TrdPlatformModelRefAddVo trdPlatformModelRefAddVo, String bladeAuth) {
        TrdPlatformModelRefEntity entity = BeanUtil.copyProperties(trdPlatformModelRefAddVo, TrdPlatformModelRefEntity.class);
        Date current = new Date();
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

    public TrdPlatformModelRefEntity updateEntity(Long id, TrdPlatformModelRefAddVo trdPlatformModelRefAddVo, String bladeAuth) {
        TrdPlatformModelRefEntity entity = BeanUtil.copyProperties(trdPlatformModelRefAddVo, TrdPlatformModelRefEntity.class);
        entity.setId(id);
        Date current = new Date();
        entity.setUpdateTime(current);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setUpdateUser(account);
        }
        return entity;
    }

}
