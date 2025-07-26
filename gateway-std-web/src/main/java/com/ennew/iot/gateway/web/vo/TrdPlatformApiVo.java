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
@Schema(description = "Api基本信息")
public class TrdPlatformApiVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 平台code
     */
    @Schema(description = "平台code")
    private String pCode;

    /**
     * 配置类型 ：认证、数据、分页
     */
    @Schema(description = "配置类型:认证、数据、分页")
    private Integer apiType;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    private String apiName;

    /**
     * 接口URL
     */
    @Schema(description = "接口URL")
    private String fullUrl;

    /**
     * 接口方法
     */
    @Schema(description = "接口方法")
    private String method;

    /**
     * 功能类型 1 上数 2 下控
     */
    @Schema(description = "功能类型[1:上数,2:下控]")
    private Integer functionType;

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
