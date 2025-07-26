package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Api信息")
public class TrdPlatformApiAddVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台code
     */
    @Schema(description = "平台code")
    @NotNull(message = "平台code不能为空")
    private String platformCode;

    /**
     * 配置类型 ：认证、数据、分页
     */
    @Schema(description = "配置类型")
    @NotNull(message = "配置类型不能为空")
    private Integer apiType;

    /**
     * 接口名称
     */
    @Schema(description = "接口名称")
    @NotNull(message = "接口名称不能为空")
    @Pattern(regexp = RegexConstant.NAME_PATTER, message = RegexConstant.NAME_ILLEGAL_MESSAGE)
    private String apiName;

    /**
     * 接口URL
     */
    @Schema(description = "接口URL")
    @NotNull(message = "接口URL不能为空")
    private String fullUrl;

    /**
     * 接口方法
     */
    @Schema(description = "接口方法")
    @NotNull(message = "接口方法不能为空")
    private String method;

    /**
     * body解析方式 jsonPath groovy 无
     */
    @Schema(description = "body解析方式")
    @NotNull(message = "body解析方式不能为空")
    private Integer bodyAnalysisType;

    /**
     * body解析代码
     */
    @Schema(description = "body解析代码")
    private String bodyAnalysisCode;

    /**
     * 是否分页
     */
    @Schema(description = "是否分页(0否,1是)")
    @NotNull(message = "是否分页不能为空")
    private Integer hasPages;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 每页大小key
     */
    @Schema(description = "每页大小key")
    private String pageSizeKey;

    /**
     * 页码key
     */
    @Schema(description = "页码key")
    private String pageNumberKey;

    /**
     * 页码起始值
     */
    @Schema(description = "页码起始值")
    private Integer pageStartNo;

    /**
     * 分页参数位置
     */
    @Schema(description = "分页参数位置")
    private Integer pagePosition;

    /**
     * 数据总数量获取方式 0 固定 1 原始接口 2 单独接口
     */
    @Schema(description = "数据总数量获取方式")
    private Integer totalNumberType;

    /**
     * 数据总数量配置值 总数或者接口ID
     */
    @Schema(description = "数据总数量配置值")
    private String totalNumberConfig;

    /**
     * 功能类型 1 上数 2 下控
     */
    @Schema(description = "功能类型")
    private Integer functionType;

    /**
     * 认证方式 1 无 2 token
     */
    @Schema(description = "认证方式(1:无,2:token)")
    @NotNull(message = "认证方式不能为空")
    private Integer authType;

    /**
     * 认证接口
     */
    @Schema(description = "认证接口Api")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authApi;

    /**
     * 限流配置 次/s
     */
    @Schema(description = "限流配置(次/s)")
    private Double callLimit;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Pattern(regexp = RegexConstant.CONTENT_PATTER, message = RegexConstant.CONTENT_ILLEGAL_MESSAGE)
    private String remark;

    @Schema(description = "参数")
    @Valid
    List<TrdPlatformApiParamAddVo> paramList;

}
