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
@Schema(description = "Api参数信息")
public class TrdPlatformApiParamVo implements Serializable {

    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "接口ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long apiId;

    @Schema(description = "参数Key")
    private String paramKey;

    @Schema(description = "参数类型")
    private Integer paramType;

    @Schema(description = "参数位置")
    private Integer paramPosition;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "备注")
    private String remark;

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

}
