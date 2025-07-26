package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value = "dev_protocol", autoResultMap = true)
public class ProtocolSupportEntity implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String type;

    private Integer way;

    private String description;

    private Byte state;

    // 是否模板[0:是,1:否]
    private Byte isTemplate;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> configuration;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //修改时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //删除状态[0:未删除,1:删除]
    @TableLogic
    private Integer isDeleted;

}
