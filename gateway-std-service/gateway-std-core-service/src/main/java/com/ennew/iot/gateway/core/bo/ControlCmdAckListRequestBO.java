package com.ennew.iot.gateway.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author ruanhong
 */
@Data
@AllArgsConstructor
public class ControlCmdAckListRequestBO implements Serializable {

    //   @Schema(name  = "不可空，设备唯一标识设备ID", required = true, example = "96465")
    public String dev;
    //   @Schema(name  = "不可空，指令序列号", required = true, example = "1011101")
    public String seq;
    //   @Schema(name  = "页数，默认为1", example = "1")
    public Integer pageNumber = 1;
    //   @Schema(name  = "每页大小，默认为10", example = "10")
    public Integer pageSize = 10;



}


