package com.enn.iot.dtu.protocol.api.codec.dto;

import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.SUCCESS;

@Data
@EqualsAndHashCode()
public class IotCmdResp {
    /**
     * 关联的指令请求
     */
    private AbstractIotCmdReq cmdReq;
    /**
     * 解析结果状态码
     */
    private String code;
    /**
     * 消息
     */
    private String message;
    /**
     * 测点信息
     */
    private List<IotCmdRespPoint> pointList;

    private IotCmdResp() {}

    public static IotCmdResp success(AbstractIotCmdReq cmdReq, List<IotCmdRespPoint> pointList) {
        IotCmdResp result = new IotCmdResp();
        result.cmdReq = cmdReq;
        result.code = SUCCESS.toString();
        result.message = SUCCESS.getMsg();
        result.pointList = pointList;
        return result;
    }

    public static IotCmdResp error(AbstractIotCmdReq cmdReq, IotDecodeCodeEnum decodeCodeEnum) {
        IotCmdResp result = new IotCmdResp();
        result.cmdReq = cmdReq;
        result.code = decodeCodeEnum.toString();
        result.message = decodeCodeEnum.getMsg();
        result.pointList = Collections.emptyList();
        return result;
    }

    public static IotCmdResp error(AbstractIotCmdReq cmdReq, IotDecodeCodeEnum decodeCodeEnum, String errorMessage) {
        IotCmdResp result = new IotCmdResp();
        result.cmdReq = cmdReq;
        result.code = decodeCodeEnum.toString();
        result.message = errorMessage;
        result.pointList = Collections.emptyList();
        return result;
    }

    /**
     *
     * @return
     */
    public boolean isSuccess() {
        return SUCCESS.toString().equals(code);
    }
}
