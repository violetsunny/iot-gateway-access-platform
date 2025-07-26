package com.ennew.iot.gateway.web.vo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "三方云平台测点映射新增信息")
public class TrdPlatformMeasureRefAddVo implements Serializable {

    @Schema(description = "平台编码")
    @NotNull
    private String platformCode;

    @Schema(description = "模型映射ID")
    @NotNull
    private Long modelRefId;

    @Schema(description = "恩牛模型标识")
    @NotNull
    private String ennModelCode;

    @Schema(description = "恩牛测点Id")
    @NotNull
    private String ennMeasureId;

    @Schema(description = "恩牛测点标识")
    @NotNull
    private String ennMeasureCode;

    @Schema(description = "恩牛测点名称")
    @NotNull
    private String ennMeasureName;

    @Schema(description = "恩牛测点单位")
    private String ennMeasureUnit;

    @Schema(description = "平台测点名称")
    @NotNull
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = "平台测点名称 " + RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String platformMeasureName;

    @Schema(description = "平台测点编码")
    @NotNull
    @Pattern(regexp = RegexConstant.CODE_PATTER, message = "平台测点编码 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    private String platformMeasureCode;

    @Schema(description = "平台测点单位")
    private String platformMeasureUnit;

    @Schema(description = "备注")
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = "备注 " + RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String remark;

    public TrdPlatformMeasureRefEntity createEntity(TrdPlatformMeasureRefAddVo trdPlatformMeasureRefAddVo, String bladeAuth) {
        TrdPlatformMeasureRefEntity entity = BeanUtil.copyProperties(trdPlatformMeasureRefAddVo, TrdPlatformMeasureRefEntity.class);
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

    public TrdPlatformMeasureRefEntity updateEntity(Long id, TrdPlatformMeasureRefAddVo trdPlatformMeasureRefAddVo, String bladeAuth) {
        TrdPlatformMeasureRefEntity entity = BeanUtil.copyProperties(trdPlatformMeasureRefAddVo, TrdPlatformMeasureRefEntity.class);
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
