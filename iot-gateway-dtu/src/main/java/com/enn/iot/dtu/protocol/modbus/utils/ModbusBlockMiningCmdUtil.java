package com.enn.iot.dtu.protocol.modbus.utils;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.modbus.constant.DataTypeEnum;
import com.enn.iot.dtu.protocol.modbus.dto.IotCmdReqPoint4Modbus;
import com.enn.iot.dtu.protocol.modbus.dto.IotReadCmdReq4Modbus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;
import static com.enn.iot.dtu.protocol.modbus.utils.ModbusGenerateCmdUtil.logContext;

/**
 * 块采地址合并工具类
 *
 * @author Mr.Jia
 * @date 2022/7/20 6:01 PM
 */
@Slf4j
public class ModbusBlockMiningCmdUtil {
    /**
     * 块采地址合并
     *
     * @param iotCmdReqList 单个测点帧数据集合
     * @return java.util.List<com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq> 块采地址合并之后的集合
     * @author Mr.Jia
     * @date 2022/1/12 13:37
     */
    public static List<AbstractIotCmdReq> generateBlockMiningReadCmdList(List<IotReadCmdReq4Modbus> iotCmdReqList) {
        // 声明块采地址合并之后的集合
        List<AbstractIotCmdReq> cmdReqList = new ArrayList<>();
        // 校验是否为空，为空的话直接返回，不做处理。
        if (CommonUtils.listIsEmpty(iotCmdReqList)) {
            return cmdReqList;
        }
        // 不符合块采范围的集合
        List<IotReadCmdReq4Modbus> qualifiedCmdReqList = new ArrayList<>();
        // 需要单采的指令集合
        List<IotReadCmdReq4Modbus> oneCmdReqList = new ArrayList<>();
        Map<String, Map<Integer, List<IotReadCmdReq4Modbus>>> sortGroupCmdMap =
            iotCmdReqList.stream().filter(iotCmdReq4Modbus -> null != iotCmdReq4Modbus.getRegisterStartAddress())
                // 1、按设备地址、寄存器地址排序
                .sorted(Comparator.comparing(IotReadCmdReq4Modbus::getCommcAddr)
                    .thenComparing(IotReadCmdReq4Modbus::getRegisterStartAddress))
                // 2、按设备地址、功能码分组
                .collect(Collectors.groupingBy(IotReadCmdReq4Modbus::getCommcAddr,
                    Collectors.groupingBy(IotReadCmdReq4Modbus::getFunctionCode)));
        // 2、重新组织块采的指令集合
        sortGroupCmdMap
            .forEach((commaAdds, funCodeSortMap) -> funCodeSortMap.forEach((functionCode, cmdReq4ModbusList) -> {
                IotReadCmdReq4Modbus cmdReqStart = cmdReq4ModbusList.get(0);
                if (cmdReqStart != null) {
                    // 最大组帧长度
                    int framingLength = cmdReqStart.getFramingLength() == 0 ? FRAMING_LENGTH_LEN_DEFAULT
                        : cmdReqStart.getFramingLength();
                    IotReadCmdReq4Modbus cmdReq = new IotReadCmdReq4Modbus();
                    cmdReq.setCommcPrcl(cmdReqStart.getCommcPrcl());
                    cmdReq.setCommcAddr(cmdReqStart.getCommcAddr());
                    cmdReq.setGatewaySn(cmdReqStart.getGatewaySn());
                    cmdReq.setFunctionCode(functionCode);
                    cmdReq.setFramingLength(framingLength);
                    cmdReq.setRegisterStartAddress(cmdReqStart.getRegisterStartAddress());
                    cmdReq.setReadonly(true);
                    cmdReq.setDelayDefensive(cmdReqStart.getDelayDefensive());
                    cmdReq.setStationId(cmdReqStart.getStationId());
                    cmdReq.setTrdPtyCode(cmdReqStart.getTrdPtyCode());
                    List<IotCmdReqPoint4Modbus> pointList = new ArrayList<>();
                    cmdReq4ModbusList.forEach(iotCmdReqAfter -> {
                        List<IotCmdReqPoint4Modbus> afterPointList = iotCmdReqAfter.getPointList();
                        if (!CommonUtils.listIsEmpty(afterPointList)) {
                            afterPointList.forEach(iotCmdReqPoint -> {
                                // 为1，不进行（禁用）批量采集；
                                if (framingLength == 1) {
                                    oneCmdReqList.add(iotCmdReqAfter);
                                    return;
                                }
                                int number = IotByteUtils.calculateRegisterNumberForRegisterRegion(
                                    DataTypeEnum.getInstance(iotCmdReqPoint.getDataType()));
                                int endAddress;
                                // 针对01、02 功能码的块采长度是线圈数量；针对其他03、04功能码的块采长度是寄存器数量；
                                if ((functionCode == FUN_CODE_READ_01) || (functionCode == FUN_CODE_READ_02)) {
                                    endAddress = iotCmdReqPoint.getRegisterAddress();
                                } else {
                                    endAddress = iotCmdReqPoint.getRegisterAddress() + number - 1;
                                }

                                // 如果数据类型长度大于此最大组帧长度参数，则不进行批量采集；
                                if ((functionCode == FUN_CODE_READ_03) || (functionCode == FUN_CODE_READ_04)) {
                                    if (number > framingLength) {
                                        oneCmdReqList.add(iotCmdReqAfter);
                                        return;
                                    }
                                }

                                // 判断测点的结束地址是否在块采范围内
                                // 块采地址范围 = [寄存器开始地址, (寄存器开始地址 + 最大组帧长度 - 1)]
                                if (cmdReqStart.getRegisterStartAddress() <= endAddress
                                    && endAddress <= cmdReqStart.getRegisterStartAddress() + framingLength - 1) {
                                    pointList.add(iotCmdReqPoint);
                                } else {
                                    qualifiedCmdReqList.add(iotCmdReqAfter);
                                }
                            });
                        }
                    });

                    // 判断不为空，才进行块采
                    if (!CommonUtils.listIsEmpty(pointList)) {
                        cmdReq.setPointList(
                            pointList.stream().sorted(Comparator.comparing(IotCmdReqPoint4Modbus::getRegisterAddress))
                                .collect(Collectors.toList()));
                        // 指令请求的寄存器数量 = 最后一个测点的”寄存器结束地址“ - 第一个测点的”寄存器开始地址“）+ 1
                        IotCmdReqPoint4Modbus cmdReqEnd = pointList.get(pointList.size() - 1);
                        IotCmdReqPoint4Modbus cmdReqOne = pointList.get(0);
                        // 针对01、02功能码的块采长度是线圈数量；针对其他03、04功能码的块采长度是寄存器数量；
                        if ((functionCode == FUN_CODE_READ_01) || (functionCode == FUN_CODE_READ_02)) {
                            cmdReq
                                .setRegisterNumber(cmdReqEnd.getRegisterAddress() - cmdReqOne.getRegisterAddress() + 1);
                        } else {
                            // 最后一个测点的”寄存器结束地址“ = 最后一个测点的寄存器开始地址 + 寄存器长度 - 1
                            int cmdReqEndAddress =
                                cmdReqEnd.getRegisterAddress() + IotByteUtils.calculateRegisterNumberForRegisterRegion(
                                    DataTypeEnum.getInstance(cmdReqEnd.getDataType())) - 1;
                            cmdReq.setRegisterNumber(cmdReqEndAddress - cmdReqOne.getRegisterAddress() + 1);
                        }

                        // 校验合法性
                        Map<String, String> errorMap = cmdReq.validate();
                        if (errorMap.isEmpty()) {
                            cmdReqList.add(cmdReq);
                        } else {
                            if (log.isWarnEnabled()) {
                                log.warn(logContext(cmdReq.getGatewaySn()) + "[Modbus] 生成【块采】读指令失败，系统别名: {}, 失败原因: {}",
                                    errorMap);
                            }
                        }
                    }
                }
            }));

        // 不符合块采范围的集合不为空的话就继续循环组装
        if (!CommonUtils.listIsEmpty(qualifiedCmdReqList)) {
            if (qualifiedCmdReqList.size() > 1) {
                cmdReqList.addAll(generateBlockMiningReadCmdList(qualifiedCmdReqList));
            } else if (qualifiedCmdReqList.size() == 1) {
                cmdReqList.addAll(qualifiedCmdReqList);
            }
        }
        if (!CommonUtils.listIsEmpty(oneCmdReqList)) {
            cmdReqList.addAll(oneCmdReqList);
        }
        return cmdReqList;
    }
}
