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

@Data
@NoArgsConstructor
@Schema(description = "三方云平台测点映射信息")
public class TrdPlatformMeasureRefVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "平台编码")
    private String platformCode;

    @Schema(description = "模型映射ID")
    private Long modelRefId;

    @Schema(description = "恩牛模型标识")
    private String ennModelCode;

    @Schema(description = "恩牛测点Id")
    private String ennMeasureId;

    @Schema(description = "恩牛测点标识")
    private String ennMeasureCode;

    @Schema(description = "恩牛测点名称")
    private String ennMeasureName;

    @Schema(description = "恩牛测点单位")
    private String ennMeasureUnit;

    @Schema(description = "平台测点名称")
    private String platformMeasureName;

    @Schema(description = "平台测点编码")
    private String platformMeasureCode;

    @Schema(description = "平台测点单位")
    private String platformMeasureUnit;

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
