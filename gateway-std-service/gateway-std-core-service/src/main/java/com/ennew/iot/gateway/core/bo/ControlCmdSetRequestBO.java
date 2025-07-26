package com.ennew.iot.gateway.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ruanhong
 */
@Data
@AllArgsConstructor
public class ControlCmdSetRequestBO implements Serializable {

    //(name = "可空，调用方系统APPID")
    public String source;
    //(name = "可空，租户ID，用来区分相同source下的不同企业")
    public String tenantId;
    //(name = "可空，设备组概念", example = "group1")
    public String sysId;
    //(name = "不可空，设备唯一标识设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "96465")
    public String dev;
    //(name = "不可空，指令下发测点名称", requiredMode = Schema.RequiredMode.REQUIRED)
    public String m;
    //(name = "不可空，指令下发测点值", requiredMode = Schema.RequiredMode.REQUIRED)
    public Object v;
    //(name = "期望发送时间，为空时表示立即发送，不为空表示定时发送", example = "yyyy-MM-dd HH:mm:ss")
    public Date expectTime;
    //(name = "服务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    public String serviceCode;


}


