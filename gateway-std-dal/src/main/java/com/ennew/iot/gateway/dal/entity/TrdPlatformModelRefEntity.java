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
 * @description 三方平台-模型映射表
 * @date 2024-04-08
 */

@Data
@TableName(value = "trd_platform_model_ref", autoResultMap = true)
public class TrdPlatformModelRefEntity implements Serializable {

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
     * 恩牛模型来源
     */
    private String ennModelSource;

    /**
     * 恩牛模型Id
     *
     */
    private String ennModelId;

    /**
     * 恩牛模型名称
     */
    private String ennModelName;

    /**
     * 恩牛模型标识
     */
    private String ennModelCode;

    /**
     * 产品ID
     */
    private String ennProductId;

    /**
     * 恩牛产品名称
     */
    private String ennProductName;

    /**
     * 平台模型名称
     */
    private String platformModelName;

    /**
     * 平台模型编码
     */
    private String platformModelCode;

    /**
     * 平台品牌名称
     */
    private String platformBrand;

    /**
     * 平台型号
     */
    private String platformSpec;

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