package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.util.Date;


@Data
public class ControlCmdAckBO {


    // @ApiModelProperty(("流水号")
    private String seq;

    // @ApiModelProperty(("设备ID")
    private String devId;

    // @ApiModelProperty(("ack内容")
    private String content;

    // @ApiModelProperty(("指令类型")
    private String cmdType;

    // @ApiModelProperty(("创建时间")
    private Date createTime;

    // @ApiModelProperty(("发送时间")
    private Date sendTime;

    // @ApiModelProperty(("响应时间")
    private Date ackTime;

    // @ApiModelProperty(("备注")
    private String remark;
}
