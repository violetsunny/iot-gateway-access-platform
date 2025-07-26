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
@Schema(description = "Api详细信息")
public class TrdPlatformApiDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
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
    @Schema(description = "配置类型")
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
     * 是否有参数
     */
    @Schema(description = "是否有参数")
    private Integer hasParam;

    /**
     * body解析方式 jsonPath groovy 无
     */
    @Schema(description = "body解析方式")
    private Integer bodyAnalysisType;

    /**
     * body解析代码
     */
    @Schema(description = "body解析代码")
    private String bodyAnalysisCode;

    /**
     * 是否分页
     */
    @Schema(description = "是否分页")
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
    @Schema(description = "数据总数量配置值,总数或者接口ID")
    private String totalNumberConfig;

    /**
     * 功能类型 1 上数 2 下控
     */
    @Schema(description = "功能类型")
    private Integer functionType;

    /**
     * 认证方式 1 无 2 token
     */
    @Schema(description = "认证方式")
    private Integer authType;

    @Schema(description = "认证接口")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authApi;

    @Schema(description = "限流配置 次/s")
    private Double callLimit;

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

    @Schema(description = "参数")
    List<TrdPlatformApiParamVo> paramList;

}
