package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value = "tripartite_platform_info", autoResultMap = true)
public class TripartitePlatformInfoEntity implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    //租户ID
    private String tenantId;

    //三方平台名称
    private String name;

    //三方平台Code
    private String code;

    //平台BaseUrl
    private String baseUrl;

    //扩展内容
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, String> content;

    //描述
    private String description;

    //创建人
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //更新人
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;

    //修改时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //删除状态[0:未删除,1:删除]
    @TableLogic
    private Integer isDeleted;

}
