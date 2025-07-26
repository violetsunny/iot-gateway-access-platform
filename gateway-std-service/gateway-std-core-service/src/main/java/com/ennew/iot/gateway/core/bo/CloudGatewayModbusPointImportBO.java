package com.ennew.iot.gateway.core.bo;

import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import lombok.Data;

import java.util.Date;

@Data
public class CloudGatewayModbusPointImportBO {

    private String user;

    private String realDeviceName;

    private Integer sort;

    private String pointName;

    private String remark;

    private String gatewayCode;

    private String functionCode;

    private String registerAddress;

    private String dataType;

    private String byteOrder;

    private String rw;

    private String productId;

    private String deviceId;

    private String deviceMetric;

    private Integer row;





    public CloudGatewayPointEntity createCloudGatewayPointEntity() {
        CloudGatewayPointEntity entity = new CloudGatewayPointEntity();
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUser(user);
        entity.setUpdateUser(user);
        entity.setRealDeviceName(this.realDeviceName);
        entity.setSort(sort);
        entity.setName(this.pointName);
        entity.setRemark(this.remark);
        entity.setCloudGatewayCode(gatewayCode);
        JSONObject config = new JSONObject();
        config.put("functionCode", this.functionCode);
        config.put("registerAddress", this.registerAddress);
        config.put("dataType", this.dataType);
        config.put("byteOrder", this.byteOrder);
        config.put("rw", this.rw);
        entity.setConfigJson(config.toJSONString());
        return entity;
    }



    public CloudGatewayDeviceEntity createCloudGatewayDeviceEntity(){
        CloudGatewayDeviceEntity deviceEntity = new CloudGatewayDeviceEntity();
        deviceEntity.setCloudGatewayCode(this.gatewayCode);
        deviceEntity.setDeviceId(this.deviceId);
        Date now = new Date();
        deviceEntity.setCreateTime(now);
        deviceEntity.setUpdateTime(now);
        deviceEntity.setCreateUser(user);
        deviceEntity.setUpdateUser(user);
        return deviceEntity;
    }

    public CloudGatewayPointMappingEntity createCloudGatewayPointMappingEntity(){
        CloudGatewayPointMappingEntity pointMappingEntity = new CloudGatewayPointMappingEntity();
        pointMappingEntity.setMetric(this.deviceMetric);
        pointMappingEntity.setProductId(this.productId);
        pointMappingEntity.setCloudGatewayCode(this.gatewayCode);
        pointMappingEntity.setDeviceId(this.deviceId);
        return pointMappingEntity;
    }
}
