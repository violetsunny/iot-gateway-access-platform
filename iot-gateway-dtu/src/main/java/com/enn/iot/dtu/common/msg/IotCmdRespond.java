
package com.enn.iot.dtu.common.msg;

import lombok.Data;
import lombok.ToString;

/**
 * 往 kafka 推送执行指令结果对象
 * <p>
 * { "stationId": "PARK1409_EMS17", "trdPtyCode": "ACOP01", "metric": "ORDctrlRmt", "seq": 305, "value": "2.0", "ts":
 * 1657831208, "sendts": 1657831208, "recvts": 1657831208, "cmdresult": "succ", "sendframe": "01 06 00 09 00 02 d8 09 ",
 * "recvframe": "01 06 00 09 00 02 d8 09 ", "respondmessage": "执行成功", "gatewaySerialNum": "FF01T_866193055397919" }
 *
 * @author Mr.Jia
 * @date 2022/8/9 6:15 PM
 */
@Data
@ToString
public class IotCmdRespond {
    /**
     * 执行结果: 成功：succ / 失败：fail / 超时：timeout
     */
    private String cmdresult;

    private String gatewaySerialNum;

    private String metric;

    /**
     * 发送指令的报文
     */
    private String sendframe;

    /**
     * 收到设备回执的报文
     */
    private String recvframe;

    /**
     * 接受指令的时间戳
     */
    private Long sendts;

    /**
     * DTU回执的时间戳
     */
    private Long recvts;

    /**
     * 执行结果消息
     */
    private String respondmessage;

    /**
     * 云网关从接受指令到执行结束的写指令耗时
     */
    private Long costMs;

    private Long seq;

    private String stationId;

    private String trdPtyCode;

    /**
     * service-open 服务接受指令的时间
     */
    private Long ts;

    private String value;
}
