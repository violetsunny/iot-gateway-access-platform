package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class ControlCmdHistoryRequestVo implements Serializable {

    @Schema(description  = "不可空，设备唯一标识设备ID", required = true, example = "96465")
    public String dev;
   @Schema(description = "指令类型，cmd/set或cmd/service", example = "cmd/set")
    public String type = "cmd/set";
   @Schema(description = "可空，指令序列号")
    public String seq;
   @Schema(description = "可空，来源APPID")
    public String source;
   @Schema(description = "可空，租户ID，用来区分相同source下的不同企业")
    public String tenantId;
   @Schema(description = "页数，默认为1", required = false, example = "1")
    public Integer pageNumber = 1;
   @Schema(description = "每页大小，默认为10", required = false, example = "10")
    public Integer pageSize = 10;
   @Schema(description = "发送时间范围 开始", required = false, example = "yyyy-MM-dd HH:mm:ss")
    public Date timeRangeStart;
   @Schema(description = "发送时间范围 结束", required = false, example = "yyyy-MM-dd HH:mm:ss")
    public Date timeRangeEnd;
   @Schema(description = "服务编码", required = false)
    public String serviceCode;
    public boolean checkValid(){
        if(StringUtils.isBlank(getDev())) {
            return false;
        }
        return true;
    }
}


