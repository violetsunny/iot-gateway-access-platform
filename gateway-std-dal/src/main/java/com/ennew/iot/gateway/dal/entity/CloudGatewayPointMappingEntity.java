package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@ToString
@TableName(value = "cloud_gateway_point_mapping", autoResultMap = true)
public class CloudGatewayPointMappingEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 网关点位ID
     */
    private Long pointId;


    /**
     * 平台测点
     */
    private String metric;


    /**
     * 平台设备ID
     */
    private String deviceId;


    /**
     * 平台产品编码
     */
    private String productId;


    /**
     * 网关编码
     */
    private String cloudGatewayCode;


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
     * 是否删除[0:未删除，1:删除]
     */
    @TableLogic
    private Integer isDelete;

}
