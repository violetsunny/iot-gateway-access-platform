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
 * @description trd_platform_api_param
 * @date 2024-03-13
 */
@Data
@TableName(value = "trd_platform_api_param", autoResultMap = true)
public class TrdPlatformApiParamEntity implements Serializable {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 接口ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long apiId;

    /**
     * 参数key
     */
    private String paramKey;

    /**
     * 参数类型
     */
    private Integer paramType;

    /**
     * 参数位置
     */
    private Integer paramPosition;

    /**
     * 参数值
     */
    private String paramValue;

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