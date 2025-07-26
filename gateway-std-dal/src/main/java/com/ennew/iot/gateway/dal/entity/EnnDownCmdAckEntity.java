package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 下行指令记录ack
 * </p>
 *
 * @author lyz
 * @since 2023-03-20
 */
@Data
@TableName("enn_down_cmd_ack")
//@ApiModel(value = "EnnDownCmdAck对象", description = "下行指令记录ack")
public class EnnDownCmdAckEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // @ApiModelProperty(("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

//    @Override
//    public String toString() {
//        return "EnnDownCmdAck{" +
//        "seq = " + seq +
//        ", devId = " + devId +
//        ", content = " + content +
//        ", cmdType = " + cmdType +
//        ", createTime = " + createTime +
//        ", sendTime = " + sendTime +
//        ", ackTime = " + ackTime +
//        ", remark = " + remark +
//        "}";
//    }
}
