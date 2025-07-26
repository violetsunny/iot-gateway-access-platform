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
@TableName(value = "cloud_gateway_point", autoResultMap = true)
public class CloudGatewayPointEntity {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 真实设备名称
     */
    private String realDeviceName;


    /**
     * 测点顺序
     */
    private Integer sort;


    /**
     * 测点名称
     */
    private String name;


    /**
     * 连接器类型
     */
    private Integer connectorType;


    /**
     * 网关编码
     */
    private String cloudGatewayCode;


    /**
     * 备注
     */
    private String remark;


    /**
     * 测点配置
     */
    private String configJson;

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
