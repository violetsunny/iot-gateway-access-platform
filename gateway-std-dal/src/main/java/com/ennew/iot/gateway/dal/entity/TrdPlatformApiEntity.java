package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ruanhong
 * @description trd_platform_api
 * @date 2024-03-13
 */

@Data
@TableName(value = "trd_platform_api", autoResultMap = true)
public class TrdPlatformApiEntity implements Serializable {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 配置类型 ：认证、数据、分页
     */
    private Integer apiType;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口URL
     */
    private String fullUrl;

    /**
     * 接口方法
     */
    private String method;

    /**
     * 是否有参数
     */
    private Integer hasParam;

    /**
     * body解析方式 jsonPath groovy 无
     */
    private Integer bodyAnalysisType;

    /**
     * body解析代码
     */
    private String bodyAnalysisCode;

    /**
     * 是否分页
     */
    private Integer hasPages;
    /**
     * 每页大小
     */
    private Integer pageSize;
    /**
     * 每页大小key
     */
    private String pageSizeKey;
    /**
     * 页码key
     */
    private String pageNumberKey;
    /**
     * 页码起始值
     */
    private Integer pageStartNo;
    /**
     * 分页参数位置
     */
    private Integer pagePosition;
    /**
     * 数据总数量获取方式 0 固定 1 原始接口 2 单独接口
     */
    private Integer totalNumberType;

    /**
     * 数据总数量配置值 总数或者接口ID
     */
    private String totalNumberConfig;

    /**
     * 功能类型 1 上数 2 下控
     */
    private Integer functionType;

    /**
     * 认证方式 1 无 2 token
     */
    private Integer authType;

    /**
     * 认证接口
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authApi;

    /**
     * 限流配置 次/s
     */
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
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除[0:未删除，1:删除]
     */
    @TableLogic
    private Integer isDelete;

}