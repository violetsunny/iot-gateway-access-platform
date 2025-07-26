package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@Schema(description = "三方平台信息")
public class TripartitePlatformInfoVo implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "租户Id")
    private String tenantId;

    @Schema(description = "三方平台Code")
    @NotNull
    private String code;

    @Schema(description = "三方平台名称")
    @NotNull
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String name;

    @Schema(description = "平台BaseUrl")
    @NotNull
    @Pattern(regexp = RegexConstant.URL_PATTER, message = RegexConstant.URL_ILLEGAL_MESSAGE)
    private String baseUrl;

    @Schema(description = "扩展内容")
    private Map<String, String> content;

    @Schema(description = "描述")
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
