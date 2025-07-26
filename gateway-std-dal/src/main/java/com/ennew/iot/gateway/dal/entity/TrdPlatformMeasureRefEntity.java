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
 * @author qk
 * @description 三方平台-测点映射表
 * @date 2024-04-08
 */

@Data
@TableName(value = "trd_platform_measure_ref", autoResultMap = true)
public class TrdPlatformMeasureRefEntity implements Serializable {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 平台编码
     */
    private String platformCode;

    /**
     * 模型映射ID
     */
    private Long modelRefId;

    /**
     * 恩牛模型标识
     */
    private String ennModelCode;

    /**
     * 恩牛测点Id
     */
    private String ennMeasureId;

    /**
     * 恩牛测点标识
     */
    private String ennMeasureCode;

    /**
     * 恩牛测点名称
     */
    private String ennMeasureName;

    /**
     * 恩牛测点单位
     */
    private String ennMeasureUnit;

    /**
     * 平台测点名称
     */
    private String platformMeasureName;

    /**
     * 平台测点编码
     */
    private String platformMeasureCode;

    /**
     * 平台测点单位
     */
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