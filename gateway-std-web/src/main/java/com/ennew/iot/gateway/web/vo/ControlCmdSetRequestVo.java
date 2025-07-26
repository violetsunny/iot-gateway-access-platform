package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ruanhong
 */
@Data
public class ControlCmdSetRequestVo implements Serializable {

    @Schema(description = "可空，调用方系统APPID")
    public String source;
    @Schema(description = "可空，租户ID，用来区分相同source下的不同企业")
    public String tenantId;
    @Schema(description = "可空，设备组概念", example = "group1")
    public String sysId;
    @Schema(description = "不可空，设备唯一标识设备ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "96465")
    public String dev;
    @Schema(description = "不可空，指令下发测点名称", requiredMode = Schema.RequiredMode.REQUIRED)
    public String m;
    @Schema(description = "不可空，指令下发测点值", requiredMode = Schema.RequiredMode.REQUIRED)
    public Object v;
    @Schema(description = "期望发送时间，为空时表示立即发送，不为空表示定时发送", example = "yyyy-MM-dd HH:mm:ss")
    public Date expectTime;
    @Schema(description = "服务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    public String serviceCode;

    public boolean checkValid(){
        if(StringUtils.isBlank(getDev())) {
            return false;
        }
        if(StringUtils.isBlank(getM())) {
            return false;
        }
        if(getV()==null) {
            return false;
        }
        return true;
    }


}


