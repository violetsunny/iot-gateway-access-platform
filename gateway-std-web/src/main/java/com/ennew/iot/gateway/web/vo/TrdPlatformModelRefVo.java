package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台模型映射信息")
public class TrdPlatformModelRefVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "平台编码")
    private String platformCode;

    @Schema(description = "恩牛模型来源")
    private String ennModelSource;

    @Schema(description = "恩牛模型Id")
    private String ennModelId;

    @Schema(description = "恩牛模型名称")
    private String ennModelName;

    @Schema(description = "恩牛模型标识")
    private String ennModelCode;

    @Schema(description = "产品ID")
    private String ennProductId;

    @Schema(description = "恩牛产品名称")
    private String ennProductName;

    @Schema(description = "平台模型名称")
    private String platformModelName;

    @Schema(description = "平台模型编码")
    private String platformModelCode;

    @Schema(description = "平台品牌名称")
    private String platformBrand;

    @Schema(description = "平台型号")
    private String platformSpec;

    @Schema(description = "测点映射")
    private List<TrdPlatformMeasureRefVo> trdPlatformMeasureRefList;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

}
