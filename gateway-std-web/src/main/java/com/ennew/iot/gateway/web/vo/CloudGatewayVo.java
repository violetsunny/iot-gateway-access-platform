package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Schema(description = "云网关信息")
public class CloudGatewayVo {

    @Schema(description = "云网关ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @Schema(description = "云网关编码")
    private String cloudGatewayCode;


    @Schema(description = "云网关类型")
    private String cloudGatewayType;


    @Schema(description = "云网关名称")
    private String cloudGatewayName;

    @Schema(description = "来源")
    private String platformSource;

    /**
     * 配置参数json(appkey、域名等)
     */
    @Schema(description = "配置参数json")
    private Object configJson;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
