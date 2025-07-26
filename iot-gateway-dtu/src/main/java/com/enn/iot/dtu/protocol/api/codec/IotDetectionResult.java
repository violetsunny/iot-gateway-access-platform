package com.enn.iot.dtu.protocol.api.codec;

import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import lombok.Data;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.SUCCESS;

@Data
public class IotDetectionResult {
    /**
     * 检测码
     */
    private IotDecodeCodeEnum detectionCode;
    /**
     * 检测结果信息
     */
    private String message;
    /**
     * 完整协议帧的开始下标，从0开始。
     */
    private int frameStartIndex;
    /**
     * 完整协议帧的字节长度。
     */
    private int frameLength;

    public static IotDetectionResult success(int frameStartIndex, int frameLength) {
        IotDetectionResult result = new IotDetectionResult();
        result.setDetectionCode(IotDecodeCodeEnum.SUCCESS);
        result.setMessage(IotDecodeCodeEnum.SUCCESS.getMsg());
        result.setFrameStartIndex(frameStartIndex);
        result.setFrameLength(frameLength);
        return result;
    }

    public static IotDetectionResult error(IotDecodeCodeEnum detectionCode, String errorMsg) {
        IotDetectionResult result = new IotDetectionResult();
        result.setDetectionCode(detectionCode);
        result.setMessage(errorMsg);
        return result;
    }

    public static IotDetectionResult error(IotDecodeCodeEnum detectionCode) {
        IotDetectionResult result = new IotDetectionResult();
        result.setDetectionCode(detectionCode);
        result.setMessage(detectionCode.getMsg());
        return result;
    }

    public static IotDetectionResult ok(IotDecodeCodeEnum detectionCode) {
        IotDetectionResult result = new IotDetectionResult();
        result.setDetectionCode(detectionCode);
        result.setMessage(detectionCode.getMsg());
        return result;
    }

    /**
     *
     * @return
     */
    public boolean isSuccess() {
        return detectionCode == SUCCESS;
    }

}

