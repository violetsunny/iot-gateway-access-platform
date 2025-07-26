package com.enn.iot.dtu.protocol.modbus;

import com.enn.iot.dtu.protocol.api.codec.IotDetectionResult;
import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import com.enn.iot.dtu.protocol.api.dto.DtuCmdDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.CimPointDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import com.enn.iot.dtu.protocol.modbus.dto.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.enn.iot.dtu.protocol.modbus.utils.ModbusBlockMiningCmdUtil.generateBlockMiningReadCmdList;
import static com.enn.iot.dtu.protocol.modbus.utils.ModbusGenerateCmdUtil.*;

@Slf4j
public class ModbusRtuCodec implements IotProtocolCodec {

    /**
     * 检测应答报文帧是否正确
     *
     * @param respByteBuf 缓冲区
     * @param iotCmdReq   指令对象
     * @return com.enn.iot.dtu.protocol.api.codec.IotDetectionResult
     * @author Mr.Jia
     * @date 2022/7/22 11:19 AM
     */
    @Override
    public IotDetectionResult detectResponseFrame(ByteBuf respByteBuf, AbstractIotCmdReq iotCmdReq) {
        respByteBuf.markReaderIndex();
        AbstractModbusFrameData frameData;
        if (iotCmdReq.getReadonly()) {
            frameData = new ModbusReadFrameData().readInstance(respByteBuf);
        } else {
            frameData = new ModbusWriteFrameData().readInstance(respByteBuf);
        }
        respByteBuf.resetReaderIndex();
        if (frameData.isResultSuccess()) {
            return IotDetectionResult.success(0, frameData.calculateFrameDataLength());
        } else if (frameData.isResultOk()) {
            return IotDetectionResult.ok(frameData.resultCode);
        } else {
            return IotDetectionResult.error(frameData.resultCode);
        }
    }

    /**
     * modbus解码
     *
     * @param respByteBuf    缓冲区
     * @param abstractCmdReq 抽象指令对象
     * @return com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp
     * @author Mr.Jia
     * @date 2021/11/27 00:52
     */
    @Override
    public IotCmdResp decode(ByteBuf respByteBuf, AbstractIotCmdReq abstractCmdReq) {
        // 读指令
        if (abstractCmdReq.getReadonly()) {
            IotReadCmdReq4Modbus cmdReadReq = (IotReadCmdReq4Modbus) abstractCmdReq;
            return decodeIotReadCmdResp(respByteBuf, cmdReadReq);
        } else {
            IotWriteCmdReq4Modbus cmdWriteReq = (IotWriteCmdReq4Modbus) abstractCmdReq;
            return decodeIotWriteCmdResp(respByteBuf, cmdWriteReq);
        }
    }

    /**
     * modbus编码
     *
     * @param abstractCmdReq      抽象指令对象
     * @param previousErrorCmdReq 超时指令
     * @param alloc               缓冲区
     * @return io.netty.buffer.ByteBuf
     * @author Mr.Jia
     * @date 2022/7/22 11:13 AM
     */
    @Override
    public ByteBuf encode(AbstractIotCmdReq abstractCmdReq, AbstractIotCmdReq previousErrorCmdReq,
                          ByteBufAllocator alloc) {
        ByteBuf buffer = alloc.directBuffer(256);
        try {
            if (abstractCmdReq.getReadonly()) {
                return encodeReadByteBuf(buffer, (IotReadCmdReq4Modbus) abstractCmdReq,
                        (IotReadCmdReq4Modbus) previousErrorCmdReq);
            } else {
                return encodeWriteByteBuf(buffer, (IotWriteCmdReq4Modbus) abstractCmdReq);
            }
        } catch (Exception e) {
            log.error(logContext(abstractCmdReq.getGatewaySn()) + "[Modbus] 编码失败，未识别异常! cmdReq: " + abstractCmdReq, e);
            if (buffer != null) {
                buffer.release();
            }
            return null;
        }
    }

    /**
     * 根据档案配置，生成读指令列表。
     *
     * @param mainData 主数据对象
     * @return java.util.List<com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq>
     * @author Mr.Jia
     * @date 2022/7/22 11:20 AM
     */
    @Override
    public List<AbstractIotCmdReq> generateReadCmdListByMainData(MainDataDTO mainData) {
        if (null == mainData || null == mainData.getDeviceList() || mainData.getDeviceList().isEmpty()) {
            // dtu下无设备
            return Collections.emptyList();
        }
        List<IotReadCmdReq4Modbus> cmdReqList = new ArrayList<>();
        // 循环设备
        mainData.getDeviceList().forEach(dtuDevice -> {
            if (StringUtils.isEmpty(dtuDevice.getTrdPtyCode())) {
                return;
            }
            // 循环设备下的测点
            dtuDevice.getPointInfo().forEach(
                    cimPoint -> generateIotReadCmdReq4Modbus(cmdReqList, mainData.getGatewaySn(), cimPoint, dtuDevice));
        });
        return generateBlockMiningReadCmdList(cmdReqList);
    }

    /**
     * 根据档案配置，生成写指令列表。
     *
     * @param mainData 主数据对象
     * @return java.util.List<com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq>
     * @author Mr.Jia
     * @date 2022/7/25 11:20 AM
     */
    @Override
    public List<AbstractIotCmdReq> generateWriteCmdListByCmdData(ControlCmdDTO mainData) {
        if (null == mainData || null == mainData.getDeviceDTO() || null == mainData.getCmdDTO()) {
            return Collections.emptyList();
        }
        List<AbstractIotCmdReq> cmdReqList = new ArrayList<>();
        DtuDeviceDTO dtuDevice = mainData.getDeviceDTO();
        List<CimPointDTO> pointInfoList = dtuDevice.getPointInfo();
        DtuCmdDTO cmdDTO = mainData.getCmdDTO();
        pointInfoList.forEach(cimPoint -> generateIotWriteCmdReq4Modbus(cmdReqList, mainData.getGatewaySn(),
                cmdDTO.getValue(), cimPoint, dtuDevice));
        return cmdReqList;
    }
}
