package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value="network_config", autoResultMap = true)
public class NetworkConfigEntity {
    @TableId(value = "id")
    private String id;

    private String name;

    private String type;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String,Object> configuration;

    private String description;
}
