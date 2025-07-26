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
 * @description trd_platform_info
 * @author ruanhong
 * @date 2024-03-13
 */
@Data
@TableName(value = "trd_platform_info", autoResultMap = true)
public class TrdPlatformInfoEntity implements Serializable {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 平台类别
     */
    private Integer pType;

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 平台名字
     */
    private String pName;

    /**
     * 来源
     */
    private String pSource;

    /**
     * 配置参数json(appkey、域名等)
     */
    private String configJson;

    /**
     * 协议Id
     */
    private String protocolId;

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
     * 0 无效、1有效
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