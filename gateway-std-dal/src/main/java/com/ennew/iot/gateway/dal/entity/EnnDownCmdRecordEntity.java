package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 下行指令记录表
 * </p>
 *
 * @author lyz
 * @since 2023-03-20
 */
@Data
@ToString
@TableName("enn_down_cmd_record")
//@ApiModel(value = "EnnDownCmdRecord对象", description = "下行指令记录表")
public class EnnDownCmdRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //   // @ApiModelProperty(("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //   // @ApiModelProperty(("流水号")
    private String seq;

    //   // @ApiModelProperty(("来源")
    private String source;

    //   // @ApiModelProperty(("设备ID")
    private String devId;

//    // TODO：添加表字段 设备父id
//    private String gatewayDeviceId;
//
//    // TODO：添加表字段 云网关id
//    private String cloudGatewayId;


    //   // @ApiModelProperty(("设备传输协议")
    private String transport;

    //   // @ApiModelProperty(("命令内容")
    private String content;

    //   // @ApiModelProperty(("命令类别")
    private String cmdType;

    //   // @ApiModelProperty(("发送类别立即、定时")
    private String sendType;

    // @ApiModelProperty(("期望发送时间")
    private Date expectTime;

    // @ApiModelProperty(("创建时间")
    private Date createTime;

    // @ApiModelProperty(("发送状态")
    private Integer sendStatus;
    //发送状态定义:CmdSendStatus

    // @ApiModelProperty(("发送时间")
    private Date sendTime;

    // @ApiModelProperty(("重试次数")
    private Integer retryTimes;

    // @ApiModelProperty(("租户ID")
    private String tenantId;

    // @ApiModelProperty(("备注")
    private String remark;

    @TableField(exist = false)
    private Boolean acked;

    // @ApiModelProperty(("服务编码")
    private String serviceCode;

}
