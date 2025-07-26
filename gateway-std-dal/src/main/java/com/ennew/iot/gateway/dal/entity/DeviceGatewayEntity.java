package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value="dev_gateway", autoResultMap = true)
public class DeviceGatewayEntity {
    @TableId(value = "id")
    private String id;

    private String name;

    private String type;

    private String protocol;

    @TableField(exist = false)
    private String protocolName;

    private NetworkConfigState state;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String,Object> configuration;

   private String networkId;

   @TableField(exist = false)
   private NetworkConfigEntity networkConfig;

    private String description;
}
