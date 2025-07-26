package com.ennew.iot.gateway.web.vo;

import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.web.util.JwtUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
@Schema(description = "Modbus测点添加对象")
public class CloudGatewayModbusPointAddVo {

    /**
     * 测点设备名称
     */
    @Schema(description = "原始设备名称")
    @NotNull(message = "原始设备名称不能为空")
    private String realDeviceName;

    /**
     * 测点顺序
     */
    @Schema(description = "点位顺序")
    private Integer sort;

    /**
     * 测点名称
     */
    @Schema(description = "点位名称")
    @NotNull(message = "名称不能为空")
    private String name;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


    /**
     * 功能码
     */
    @Schema(description = "功能码")
    @NotNull(message = "功能码不能为空")
    private Integer functionCode;


    /**
     * 寄存器地址
     */
    @Schema(description = "寄存器地址")
    @NotNull(message = "寄存器地址不能为空")
    private Integer registerAddress;


    /**
     * 数据类型
     */
    @Schema(description = "数据类型")
    @NotNull(message = "数据类型不能为空")
    private String dataType;


    /**
     * 字节顺序
     */
    @Schema(description = "字节序")
    @NotNull(message = "字节顺序不能为空")
    private String byteOrder;


    /**
     * 读写类型
     */
    @Schema(description = "读写类型")
    @NotNull(message = "读写类型不能为空")
    private String rw;


    public CloudGatewayPointEntity createEntity(String gatewayCode, String bladeAuth){
        CloudGatewayPointEntity entity = new CloudGatewayPointEntity();
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setCreateUser(account);
            entity.setUpdateUser(account);
        }
        entity.setRealDeviceName(this.realDeviceName);
        entity.setSort(this.sort);
        entity.setName(this.name);
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



    public CloudGatewayPointEntity createUpdateEntity(Long id, String gatewayCode, String bladeAuth){
        CloudGatewayPointEntity entity = new CloudGatewayPointEntity();
        Date now = new Date();
        entity.setUpdateTime(now);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setUpdateUser(account);
        }
        entity.setRealDeviceName(this.realDeviceName);
        entity.setSort(this.sort);
        entity.setName(this.name);
        entity.setRemark(this.remark);
        entity.setCloudGatewayCode(gatewayCode);
        JSONObject config = new JSONObject();
        config.put("functionCode", this.functionCode);
        config.put("registerAddress", this.registerAddress);
        config.put("dataType", this.dataType);
        config.put("byteOrder", this.byteOrder);
        config.put("rw", this.rw);
        entity.setConfigJson(config.toJSONString());
        entity.setId(id);
        return entity;
    }
}
