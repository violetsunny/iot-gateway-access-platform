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
@Schema(description = "三方云平台信息")
public class TrdPlatformTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 任务code
     */
    @Schema(description = "任务code")
    private String taskCode;

    /**
     * 平台Id
     */
    @Schema(description = "平台Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pId;

    /**
     * 平台code
     */
    @Schema(description = "平台code")
    private String pCode;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String pName;

    /**
     * 平台类型
     */
    @Schema(description = "平台类型")
    private Integer pType;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务频率
     */
    @Schema(description = "任务频率")
    private String frequency;

    /**
     * 接口ID
     */
    @Schema(description = "接口ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long apiId;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private String productId;

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
