package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author ruanhong
 */
@Data
public class ControlCmdAckListRequestVo implements Serializable {

    @Schema(description = "不可空，设备唯一标识设备ID", required = true, example = "96465")
    public String dev;
    @Schema(description = "不可空，指令序列号", required = true, example = "1011101")
    public String seq;
    @Schema(description = "页数，默认为1", example = "1")
    public Integer pageNumber = 1;
    @Schema(description = "每页大小，默认为10", example = "10")
    public Integer pageSize = 10;

    public boolean checkValid(){
        if(StringUtils.isBlank(getDev())) {
            return false;
        }
        if(StringUtils.isBlank(getSeq()))  {
            return false;
        }
        return true;
    }

}


