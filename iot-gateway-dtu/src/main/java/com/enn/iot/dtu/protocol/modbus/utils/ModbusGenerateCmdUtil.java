package com.enn.iot.dtu.protocol.modbus.utils;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdRespPoint;
import com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.api.maindata.dto.CimPointDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import com.enn.iot.dtu.protocol.modbus.constant.ByteOrderEnum;
import com.enn.iot.dtu.protocol.modbus.constant.DataTypeEnum;
import com.enn.iot.dtu.protocol.modbus.dto.*;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.enn.iot.dtu.protocol.api.enums.IotDecodeCodeEnum.*;
import static com.enn.iot.dtu.protocol.modbus.constant.ModbusConstant.*;
import static com.enn.iot.dtu.protocol.modbus.utils.IotByteUtils.calculateRegisterNumberForRegisterRegion;
import static com.enn.iot.dtu.protocol.modbus.utils.IotByteUtils.setNumberFromRegisterRegion;

/**
 * modbus 生成读写指令工具类
 *
 * @author Mr.Jia
 * @date 2022/7/20 6:01 PM
 */
@Slf4j
public class ModbusGenerateCmdUtil {

    /**
     * 日志打印格式
     *
     * @param gatewaySn 网关信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    public static String logContext(String gatewaySn) {
        return "[" + gatewaySn + "]";
    }

    /**
     * 生成写指令的缓冲区
     *
     * @param buffer 缓冲区
     * @param writeCmd 写指令
     * @return io.netty.buffer.ByteBuf
     * @author Mr.Jia
     * @date 2022/7/22 11:11 AM
     */
    public static ByteBuf encodeWriteByteBuf(ByteBuf buffer, IotWriteCmdReq4Modbus writeCmd) {
        // 写指令
        Integer functionCode = writeCmd.getFunctionCode();
        buffer.writeByte(Integer.parseInt(writeCmd.getCommcAddr()));
        buffer.writeByte(functionCode);
        buffer.writeShort(writeCmd.getRegisterStartAddress());

        // 获取下发的值
        Number addressWriteValue = writeCmd.getAddressWriteValue();
        List<IotCmdReqPoint4Modbus> pointList = writeCmd.getPointList();
        if (!CommonUtils.listIsEmpty(pointList)) {
            IotCmdReqPoint4Modbus cmdReqPoint4Modbus = pointList.get(0);
            if (cmdReqPoint4Modbus != null) {
                ByteOrderEnum byteOrderEnum = ByteOrderEnum.getInstance(cmdReqPoint4Modbus.getByteOrder());
                DataTypeEnum dataTypeEnum = DataTypeEnum.getInstance(cmdReqPoint4Modbus.getDataType());
                switch (functionCode) {
                    // 05 (0x05)写单个线圈
                    case FUN_CODE_READ_05: {
                        int value = addressWriteValue.intValue();
                        // 1: 十六进制值 0XFF00 请求线圈为 ON。
                        // 0: 十六进制值 0X0000 请求线圈为 OFF。
                        // 其它所有值均为非法的，并且对线圈不起作用。
                        if (value == RTU_CMD_ADDRESS_1) {
                            buffer.writeShort(0xFF00);
                        } else {
                            buffer.writeShort(0x0000);
                        }
                        byte[] array = new byte[RTU_HEAD_WRITE_ONE_CMD_LEN];
                        buffer.getBytes(buffer.readerIndex(), array);
                        buffer.writeShortLE(ModbusRtuUtil.getCrc16ToInt(array));
                        return buffer;
                    }
                    // 06 (0x06)写单个寄存器
                    case FUN_CODE_READ_06: {
                        ByteBuf newByteBuf =
                            setNumberFromRegisterRegion(buffer, addressWriteValue, dataTypeEnum, byteOrderEnum);
                        byte[] array = new byte[RTU_HEAD_WRITE_ONE_CMD_LEN];
                        newByteBuf.getBytes(newByteBuf.readerIndex(), array);
                        newByteBuf.writeShortLE(ModbusRtuUtil.getCrc16ToInt(array));
                        return newByteBuf;
                    }

                    // 16 (0x10) 写多个寄存器
                    case FUN_CODE_READ_10: {
                        buffer.writeShort(writeCmd.getRegisterNumber());
                        buffer.writeByte(writeCmd.getByteNumber());
                        ByteBuf newByteBuf =
                            setNumberFromRegisterRegion(buffer, addressWriteValue, dataTypeEnum, byteOrderEnum);
                        byte[] array = new byte[RTU_HEAD_WRITE_BATCH_CMD_LEN + writeCmd.getByteNumber()];
                        newByteBuf.getBytes(newByteBuf.readerIndex(), array);
                        newByteBuf.writeShortLE(ModbusRtuUtil.getCrc16ToInt(array));
                        return newByteBuf;
                    }
                    default:
                        throw new IllegalArgumentException("不支持当前的写功能码，functionCode:" + functionCode);
                }
            }
        }
        return null;
    }

    /**
     * 生成读指令的缓冲区
     *
     * @param buffer 缓冲区
     * @param reqCmd 写指令
     * @param preErrorCmdReq 超时指令
     * @return io.netty.buffer.ByteBuf
     * @author Mr.Jia
     * @date 2022/7/22 11:11 AM
     */
    public static ByteBuf encodeReadByteBuf(ByteBuf buffer, IotReadCmdReq4Modbus reqCmd,
        IotReadCmdReq4Modbus preErrorCmdReq) {
        Integer functionCode = reqCmd.getFunctionCode();
        buffer.writeByte(Integer.parseInt(reqCmd.getCommcAddr()));
        buffer.writeByte(functionCode);
        // 如果previousErrorCmdReq不是空的话，代表上次指令超时，有可能乱序，则进入乱系处理逻辑
        buffer.writeShort(getRequestRegisterStartAddress(preErrorCmdReq, reqCmd));
        buffer.writeShort(reqCmd.calculateRequestRegisterNumber());
        byte[] array = new byte[RTU_HEAD_WRITE_ONE_CMD_LEN];
        buffer.getBytes(buffer.readerIndex(), array);
        buffer.writeShortLE(ModbusRtuUtil.getCrc16ToInt(array));
        return buffer;
    }

    /**
     * 读指令响应解析方法
     *
     * @param respByteBuf 缓冲区
     * @param cmdReadReq 读指令对象
     * @return com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp
     * @author Mr.Jia
     * @date 2022/7/21 3:59 PM
     */
    public static IotCmdResp  decodeIotReadCmdResp(ByteBuf respByteBuf, IotReadCmdReq4Modbus cmdReadReq) {
        respByteBuf.markReaderIndex();
        int frameDataLength = respByteBuf.readableBytes();
        ModbusReadFrameData frameData = new ModbusReadFrameData().readInstance(respByteBuf);
        respByteBuf.resetReaderIndex();
        if (!frameData.isResultSuccess()) {
            return IotCmdResp.error(cmdReadReq, frameData.resultCode);
        }
        if (frameDataLength != frameData.calculateFrameDataLength()) {
            return IotCmdResp.error(cmdReadReq, ERROR_FRAME_LENGTH_TOO_LONG);
        }
        IotDecodeCodeEnum validateResult = frameData.validateForDecode(cmdReadReq);
        if (validateResult != SUCCESS) {
            return IotCmdResp.error(cmdReadReq, validateResult);
        }
        List<IotCmdReqPoint4Modbus> pointListReq = cmdReadReq.getPointList();
        if (CommonUtils.listIsEmpty(pointListReq)) {
            return IotCmdResp.error(cmdReadReq, ERROR_CONFIG_POINT_LIST_EMPTY, "测点配置列表为空");
        }
        List<IotCmdRespPoint> pointList = new ArrayList<>();
        pointListReq.forEach(iotCmdReqPoint4Modbus -> {
            IotCmdRespPoint cmdRespPoint = new IotCmdRespPoint();
            cmdRespPoint.setPointCode(iotCmdReqPoint4Modbus.getPointCode());
            cmdRespPoint.setDeviceTrdPtyCode(iotCmdReqPoint4Modbus.getDeviceTrdPtyCode());
            cmdRespPoint.setSystemAliasCode(iotCmdReqPoint4Modbus.getSystemAliasCode());
            cmdRespPoint.setTimeMs(System.currentTimeMillis());
            int startIndex = RTU_HEAD_LEN + cmdReadReq.calculateResponseDataStartIndex();
            String inByteOrder = iotCmdReqPoint4Modbus.getByteOrder();
            // 本次想解析的线圈，在应答报文的线圈结果中排第几位（下标从0开始）
            int dynamicIndex = iotCmdReqPoint4Modbus.getRegisterAddress() - cmdReadReq.getRegisterStartAddress();
            // 默认是从第三个字节开始解析 针对功能码01 02特殊处理。
            if ((frameData.functionCode == FUN_CODE_READ_01) || (frameData.functionCode == FUN_CODE_READ_02)) {
                Number number = IotByteUtils.getBooleanFromStatusRegion(respByteBuf, startIndex + (dynamicIndex / 8),
                    dynamicIndex % 8);
                cmdRespPoint.setValue(number);
            } else {
                ByteOrderEnum byteOrder = ByteOrderEnum.getInstance(inByteOrder);
                DataTypeEnum dataType = DataTypeEnum.getInstance(iotCmdReqPoint4Modbus.getDataType());
                Number value = IotByteUtils.getNumberFromRegisterRegion(respByteBuf, startIndex + (dynamicIndex * 2),
                    byteOrder, dataType);
                cmdRespPoint.setValue(value);
            }
            pointList.add(cmdRespPoint);
        });
        return IotCmdResp.success(cmdReadReq, pointList);
    }

    /**
     * 写指令响应解析方法
     *
     * @param respByteBuf 缓冲区
     * @param cmdWriteReq 写指令对象
     * @return com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp
     * @author Mr.Jia
     * @date 2022/7/21 3:59 PM
     */
    public static IotCmdResp decodeIotWriteCmdResp(ByteBuf respByteBuf, IotWriteCmdReq4Modbus cmdWriteReq) {
        respByteBuf.markReaderIndex();
        int frameDataLength = respByteBuf.readableBytes();
        ModbusWriteFrameData frameData = new ModbusWriteFrameData().readInstance(respByteBuf);
        respByteBuf.resetReaderIndex();
        if (!frameData.isResultSuccess()) {
            return IotCmdResp.error(cmdWriteReq, frameData.resultCode);
        }
        if (frameDataLength != frameData.calculateFrameDataLength()) {
            return IotCmdResp.error(cmdWriteReq, ERROR_FRAME_LENGTH_TOO_LONG);
        }
        IotDecodeCodeEnum validateResult = frameData.validateForDecode(cmdWriteReq);
        if (validateResult != SUCCESS) {
            return IotCmdResp.error(cmdWriteReq, validateResult);
        }
        List<IotCmdReqPoint4Modbus> pointListReq = cmdWriteReq.getPointList();
        if (CommonUtils.listIsEmpty(pointListReq)) {
            return IotCmdResp.error(cmdWriteReq, ERROR_CONFIG_POINT_LIST_EMPTY, "测点配置列表为空");
        }
        List<IotCmdRespPoint> pointList = new ArrayList<>();
        pointListReq.forEach(point4Modbus -> {
            IotCmdRespPoint cmdRespPoint = new IotCmdRespPoint();
            cmdRespPoint.setPointCode(point4Modbus.getPointCode());
            cmdRespPoint.setDeviceTrdPtyCode(point4Modbus.getDeviceTrdPtyCode());
            cmdRespPoint.setSystemAliasCode(point4Modbus.getSystemAliasCode());
            cmdRespPoint.setTimeMs(System.currentTimeMillis());
            String inByteOrder = point4Modbus.getByteOrder();
            int functionCode = frameData.functionCode;
            if (functionCode == FUN_CODE_READ_05) {
                if (frameData.getAddressWriteValue().intValue() == RTU_CMD_ADDRESS_0) {
                    cmdRespPoint.setValue(RTU_CMD_ADDRESS_0);
                } else {
                    cmdRespPoint.setValue(RTU_CMD_ADDRESS_1);
                }
            } else if (functionCode == FUN_CODE_READ_06) {
                ByteOrderEnum byteOrder = ByteOrderEnum.getInstance(inByteOrder);
                DataTypeEnum dataType = DataTypeEnum.getInstance(point4Modbus.getDataType());
                Number value = IotByteUtils.getNumberFromRegisterRegion(respByteBuf, 4, byteOrder, dataType);
                cmdRespPoint.setValue(value);
            } else if (functionCode == FUN_CODE_READ_10) {
                cmdRespPoint.setValue(cmdWriteReq.getAddressWriteValue());
            }
            pointList.add(cmdRespPoint);
        });
        return IotCmdResp.success(cmdWriteReq, pointList);
    }

    /**
     * 生成写指令IotWriteCmdReq4Modbus对象信息
     *
     * @param gatewaySn 网关
     * @param addressWriteValue 下发的值
     * @param cimPoint 测点信息
     * @param dtuDevice 设备信息
     * @author Mr.Jia
     * @date 2022/7/20 16:01 PM
     */
    public static void generateIotWriteCmdReq4Modbus(List<AbstractIotCmdReq> cmdReqList, String gatewaySn,
        Number addressWriteValue, CimPointDTO cimPoint, DtuDeviceDTO dtuDevice) {
        IotCmdReqPoint4Modbus point = new IotCmdReqPoint4Modbus();
        List<IotCmdReqPoint4Modbus> pointList = new ArrayList<>();
        String dataType = getDataType(dtuDevice, cimPoint, gatewaySn);
        // 写指令功能码
        int functionWriteCode = getWriteCmdFunctionCode(dtuDevice, cimPoint, dataType, gatewaySn);
        point.setByteOrder(getByteOrder(dtuDevice, cimPoint, gatewaySn));
        point.setDataType(dataType);
        point.setFunctionCode(getReadFunctionCode(dtuDevice, cimPoint, gatewaySn));
        Integer registerStartAddress = getRegisterStartAddress(gatewaySn, cimPoint, dtuDevice, functionWriteCode);
        // 如果寄存器地址是空值,将跳过该测点，忽略采集
        if (registerStartAddress == null) {
            return;
        }
        point.setRegisterAddress(registerStartAddress);
        point.setPointCode(cimPoint.getMeasureCat());
        point.setDeviceTrdPtyCode(dtuDevice.getTrdPtyCode());
        point.setSystemAliasCode(dtuDevice.getStationId());
        pointList.add(point);
        IotWriteCmdReq4Modbus cmdReq = new IotWriteCmdReq4Modbus();
        cmdReq.setFunctionCode(functionWriteCode);
        cmdReq.setRegisterStartAddress(registerStartAddress);
        cmdReq.setGatewaySn(gatewaySn);
        cmdReq.setCommcPrcl(dtuDevice.getCommcPrcl());
        cmdReq.setRegisterNumber(getRegisterNumber(dataType, functionWriteCode));
        cmdReq.setCommcAddr(getCommcAddr(dtuDevice, gatewaySn, cimPoint));
        cmdReq.setAddressWriteValue(addressWriteValue);
        cmdReq.setReadonly(false);
        cmdReq.setStationId(dtuDevice.getStationId());
        cmdReq.setTrdPtyCode(dtuDevice.getTrdPtyCode());
        cmdReq.setFramingLength(getFramingLength(dtuDevice, gatewaySn, cimPoint));
        cmdReq.setByteNumber(getByteNumber(dataType));
        // 写指令对应的读指令
        List<IotReadCmdReq4Modbus> cmdReadReqList = new ArrayList<>();
        IotReadCmdReq4Modbus iotReadCmdReq4Modbus =
            generateIotReadCmdReq4Modbus(cmdReadReqList, gatewaySn, cimPoint, dtuDevice);
        cmdReq.setIotReadCmdReq4Modbus(iotReadCmdReq4Modbus);
        if (!CommonUtils.listIsEmpty(pointList)) {
            cmdReq.setPointList(pointList);
        }
        Map<String, String> errorMap = cmdReq.validate();
        if (errorMap.isEmpty()) {
            cmdReqList.add(cmdReq);
        } else {
            if (log.isWarnEnabled()) {
                log.warn(logContext(gatewaySn) + "[Modbus] 生成写指令失败，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 失败原因: {}",
                    dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), errorMap);
            }
        }
    }

    /**
     * 生成读指令IotCmdReq4Modbus对象信息
     *
     * @param cmdReqList 读指令集合
     * @param gatewaySn 网关
     * @param cimPoint 测点信息
     * @param dtuDevice 设备信息
     * @return com.enn.iot.dtu.protocol.modbus.dto.IotWriteCmdReq4Modbus
     * @author Mr.Jia
     * @date 2022/7/21 12:01 PM
     */
    public static IotReadCmdReq4Modbus generateIotReadCmdReq4Modbus(List<IotReadCmdReq4Modbus> cmdReqList,
        String gatewaySn, CimPointDTO cimPoint, DtuDeviceDTO dtuDevice) {
        IotCmdReqPoint4Modbus point = new IotCmdReqPoint4Modbus();
        List<IotCmdReqPoint4Modbus> pointList = new ArrayList<>();
        //TODO:4.功能码获取
        int functionCode = getReadFunctionCode(dtuDevice, cimPoint, gatewaySn);
        Integer registerStartAddress = getRegisterStartAddress(gatewaySn, cimPoint, dtuDevice, functionCode);
        // 如果寄存器地址是空值,将跳过该测点，忽略采集
        if (registerStartAddress == null) {
            return null;
        }
        String dataType = getDataType(dtuDevice, cimPoint, gatewaySn);
        point.setByteOrder(getByteOrder(dtuDevice, cimPoint, gatewaySn));
        point.setDataType(dataType);
        point.setFunctionCode(functionCode);
        point.setRegisterAddress(registerStartAddress);
        point.setPointCode(cimPoint.getMeasureCat());
        point.setDeviceTrdPtyCode(dtuDevice.getTrdPtyCode());
        //TODO:5.暂无
        point.setSystemAliasCode(dtuDevice.getStationId());
        pointList.add(point);
        IotReadCmdReq4Modbus cmdReq = new IotReadCmdReq4Modbus();
        cmdReq.setFunctionCode(functionCode);
        cmdReq.setRegisterStartAddress(registerStartAddress);
        cmdReq.setGatewaySn(gatewaySn);
        cmdReq.setCommcPrcl(dtuDevice.getCommcPrcl());
        cmdReq.setRegisterNumber(getRegisterNumber(dataType, functionCode));
        cmdReq.setCommcAddr(getCommcAddr(dtuDevice, gatewaySn, cimPoint));
        //TODO:6.默认
        cmdReq.setFramingLength(getFramingLength(dtuDevice, gatewaySn, cimPoint));
        //TODO:7.延迟防御？
        cmdReq.setDelayDefensive(getDelayDefensive(dtuDevice));
        cmdReq.setReadonly(true);
        //TODO:8.暂无
        cmdReq.setStationId(dtuDevice.getStationId());
        cmdReq.setTrdPtyCode(dtuDevice.getTrdPtyCode());
        if (!CommonUtils.listIsEmpty(pointList)) {
            cmdReq.setPointList(pointList);
        }
        Map<String, String> errorMap = cmdReq.validate();
        if (errorMap.isEmpty()) {
            cmdReqList.add(cmdReq);
        } else {
            if (log.isWarnEnabled()) {
                log.warn(logContext(gatewaySn) + "[Modbus] 生成读指令失败，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 失败原因: {}",
                    dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), errorMap);
            }
        }
        return cmdReq;
    }

    /**
     * 设备的字节序默认值
     *
     * @param dtuDevice 设备信息
     * @param cimPoint 测点信息
     * @param gatewaySn 网关信息
     * @return java.lang.String 字节序
     * @author Mr.Jia
     * @date 2022/7/22 16:01 PM
     */
    private static String getByteOrder(DtuDeviceDTO dtuDevice, CimPointDTO cimPoint, String gatewaySn) {
        String byteOrder;
        // 以测点的第一个非空字节序作为该设备的字节序，都为空时默认3412
        if (!ByteOrderEnum.validate(cimPoint.getByteOrder())) {
            byteOrder = ByteOrderEnum.getDefault().getValue();
            log.warn(logContext(gatewaySn) + "[Modbus] 字节序是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 字节序为空设置为默认值 {}。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), byteOrder);
        } else {
            byteOrder = cimPoint.getByteOrder();
        }
        return byteOrder;
    }

    /**
     * 数据类型默认值
     *
     * @param dtuDevice 设备信息
     * @param cimPoint 测点信息
     * @param gatewaySn 网关信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    private static String getDataType(DtuDeviceDTO dtuDevice, CimPointDTO cimPoint, String gatewaySn) {
        String dataType;
        // 默认uint16数据类型
        if (!DataTypeEnum.validate(cimPoint.getModBusDataType())) {
            dataType = DataTypeEnum.getDefault().getValue();
            log.warn(logContext(gatewaySn) + "[Modbus] 数据类型是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 数据类型为空设置为默认值 {}。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), dataType);
        } else {
            dataType = cimPoint.getModBusDataType();
        }
        return dataType;
    }

    /**
     * 获取读指令功能码
     *
     * @param dtuDevice 设备信息
     * @param cimPoint 测点信息
     * @param gatewaySn 网关信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    private static int getReadFunctionCode(DtuDeviceDTO dtuDevice, CimPointDTO cimPoint, String gatewaySn) {
        int functionCode;
        if ("".equals(CommonUtils.nullToString(cimPoint.getFunctionCode()))) {
            functionCode = FUN_CODE_READ_DEFAULT;
            log.warn(logContext(gatewaySn) + "[Modbus] 功能码是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 功能码为空设置为默认值 {}。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), functionCode);
        } else {
            functionCode = Integer.parseInt(cimPoint.getFunctionCode());
        }
        return functionCode;
    }

    /**
     * 根据读功能码获取写指令功能码
     *
     * @param dtuDevice 设备信息
     * @param cimPoint 测点信息
     * @param dataType 数据类型
     * @param gatewaySn 网关信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/8/15 17:42
     */
    public static int getWriteCmdFunctionCode(DtuDeviceDTO dtuDevice, CimPointDTO cimPoint, String dataType,
        String gatewaySn) {
        // 获取读功能码
        int functionReadCode = getReadFunctionCode(dtuDevice, cimPoint, gatewaySn);
        int functionWriteCode;
        if (functionReadCode == FUN_CODE_READ_01) {
            functionWriteCode = FUN_CODE_READ_05;
        } else if (functionReadCode == FUN_CODE_READ_03) {
            // 检查目前06、10 功能码支持的数据类型
            if (!checkSupportDataType(dataType)) {
                throw new IllegalArgumentException(
                    "当前测点的数据类型不支持生成对应的写功能码，dataType:" + dataType + "，functionCode:" + functionReadCode);
            }
            int byteNumber = getByteNumber(dataType);
            if (byteNumber <= 2) {
                // 数据类型对应的字节数 ≤ 2 : uint16/int16
                functionWriteCode = FUN_CODE_READ_06;
            } else {
                // 数据类型对应的字节数 > 2: uint32/int32/uint64/int64/float32/float64
                functionWriteCode = FUN_CODE_READ_10;
            }
        } else {
            throw new IllegalArgumentException("当前不支持生成写指令，当前的读功能码为:" + functionReadCode);
        }
        return functionWriteCode;
    }

    /**
     * 获取数据类型对应的字节数
     *
     * @param dataType 数据类型
     * @return int
     * @author Mr.Jia
     * @date 2022/2/22 17:49
     */
    public static int getByteNumber(String dataType) {
        return 2 * calculateRegisterNumberForRegisterRegion(DataTypeEnum.getInstance(dataType));
    }

    /**
     * 目前06、10 功能码支持的数据类型
     *
     * @param dataType 数据类型
     * @return boolean
     * @author Mr.Jia
     * @date 2022/7/29 18:17
     */
    private static boolean checkSupportDataType(String dataType) {
        List<String> dataTypeList = Arrays.asList(DataTypeEnum.UINT16.getValue(), DataTypeEnum.INT16.getValue(),
            DataTypeEnum.UINT32.getValue(), DataTypeEnum.INT32.getValue(), DataTypeEnum.UINT64.getValue(),
            DataTypeEnum.INT64.getValue(), DataTypeEnum.FLOAT32.getValue(), DataTypeEnum.FLOAT64.getValue());
        return dataTypeList.contains(dataType);
    }

    /**
     * 寄存器地址默认值
     *
     * @param cimPoint 测点信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    private static Integer getRegisterStartAddress(String gatewaySn, CimPointDTO cimPoint, DtuDeviceDTO dtuDevice,
        int functionCode) {
        String modBus = cimPoint.getModBus();
        if ("".equals(CommonUtils.nullToString(modBus))) {
            log.warn(
                logContext(gatewaySn) + "[Modbus] 寄存器地址是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 寄存器地址: {}, 将跳过该测点，忽略采集。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), modBus);
            return null;
        }
        // 地址从0开始
        Integer registerStartAddress = null;
        try {
            registerStartAddress = Integer.parseInt(modBus);
        } catch (NumberFormatException e) {
            if (log.isWarnEnabled()) {
                log.warn(logContext(gatewaySn) + "[Modbus] 寄存器地址不是有效数值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 寄存器地址: {}",
                    dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(),
                    cimPoint.getModBus());
            }
        }
        // 根据 1x, 3x, 4x 这种前缀的寄存器地址，处理寄存器地址
        return calculateRequestRegisterStartAddress(registerStartAddress, functionCode);
    }

    /**
     * 最大组帧长度默认值
     *
     * @param dtuDevice 设备信息
     * @param gatewaySn 网关信息
     * @param cimPoint 测点信息
     * @return int
     * @author Mr.Jia
     * @date 2022/7/20 5:59 PM
     */
    private static int getFramingLength(DtuDeviceDTO dtuDevice, String gatewaySn, CimPointDTO cimPoint) {
        int framingLength;
        // 最大组帧长度
        if ("".equals(CommonUtils.nullToString(dtuDevice.getFramingLength())) || dtuDevice.getFramingLength() == 0) {
            framingLength = FRAMING_LENGTH_LEN_DEFAULT;
            log.warn(logContext(gatewaySn) + "[Modbus] 最大组帧长度是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 最大组帧长度为空设置为默认值 {}。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(),
                FRAMING_LENGTH_LEN_DEFAULT);
        } else {
            framingLength = dtuDevice.getFramingLength();
        }
        return framingLength;
    }

    /**
     * 延迟防御机制默认值为 1
     * 
     * @author Mr.Jia
     * @date 2022/12/22 9:22 AM
     * @param dtuDevice
     * @return int
     */
    private static int getDelayDefensive(DtuDeviceDTO dtuDevice) {
        int delayDefensive;
        // 延迟防御 1 开 0 关 默认: 开
        if ("".equals(CommonUtils.nullToString(dtuDevice.getDelayDefensive()))) {
            delayDefensive = 1;
        } else {
            delayDefensive = dtuDevice.getDelayDefensive();
        }
        return delayDefensive;
    }

    /**
     * 采集指令中将设备通讯地址默认01
     *
     * @param dtuDevice 设备信息
     * @param gatewaySn 网关标识
     * @param cimPoint 测点信息
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    private static String getCommcAddr(DtuDeviceDTO dtuDevice, String gatewaySn, CimPointDTO cimPoint) {
        String commcAddr;
        // 采集指令中将设备通讯地址默认01
        if ("".equals(CommonUtils.nullToString(dtuDevice.getCommcAddr()))) {
            commcAddr = ProtocolTypeEnum.MODBUS_RTU.getDefaultAddress();
            log.warn(logContext(gatewaySn) + "[Modbus] 通讯地址是空值，系统别名: {}, 设备三方编码: {}, 测点编码: {}, 通讯地址为空设置为默认值 {}。",
                dtuDevice.getStationId(), dtuDevice.getTrdPtyCode(), cimPoint.getMeasureCat(), commcAddr);
        } else {
            commcAddr = dtuDevice.getCommcAddr();
        }
        return commcAddr;
    }

    /**
     * 寄存器数量
     *
     * @param dataType 数据类型
     * @param functionCode 功能码
     * @return java.lang.String
     * @author Mr.Jia
     * @date 2022/2/22 17:39
     */
    public static Integer getRegisterNumber(String dataType, int functionCode) {
        int registerNumber;
        // 针对modbus的01、02 功能码，只需按照协议规范进行编码和解码，不用依赖用户配置的数据类型和字节序,默认读取一个寄存器。
        if ((functionCode == FUN_CODE_READ_01) || (functionCode == FUN_CODE_READ_02)) {
            registerNumber = FUN_CODE_01_02_DEFAULT_VALUE;
        } else {
            registerNumber = calculateRegisterNumberForRegisterRegion(DataTypeEnum.getInstance(dataType));
        }
        return registerNumber;
    }

    /**
     * 根据 1x, 3x, 4x 这种前缀的寄存器地址，处理寄存器地址
     *
     * @param registerStartAddress 寄存器地址
     * @param functionCode 功能码
     * @return java.lang.Integer 最后计算出的寄存器地址
     * @author Mr.Jia
     * @date 2021/12/23 16:31
     */
    public static Integer calculateRequestRegisterStartAddress(Integer registerStartAddress, Integer functionCode) {
        if (registerStartAddress != null && functionCode != null) {
            char highNum = registerStartAddress.toString().charAt(0);
            if ((registerStartAddress >= 40000) && (highNum == '4') && (functionCode == FUN_CODE_READ_03
                || functionCode == FUN_CODE_READ_06 || functionCode == FUN_CODE_READ_10)) {
                return getNewAddress(registerStartAddress);
            } else if ((registerStartAddress >= 30000) && (highNum == '3')
                && (functionCode == FUN_CODE_READ_04 || functionCode == FUN_CODE_READ_05)) {
                return getNewAddress(registerStartAddress);
            } else if ((registerStartAddress >= 10000) && (highNum == '1')
                && (functionCode == FUN_CODE_READ_02 || functionCode == FUN_CODE_READ_05)) {
                return getNewAddress(registerStartAddress);
            } else {
                return registerStartAddress;
            }
        }
        return registerStartAddress;
    }

    /**
     * 针对寄存器地址进行适配
     * <p>
     * 规则：符合条件的丢掉最高位，保留剩下的作为新的寄存器地址使用
     *
     * @param registerStartAddress 寄存器地址
     * @return int
     * @author Mr.Jia
     * @date 2022/5/26 10:09
     */
    public static int getNewAddress(Integer registerStartAddress) {
        // 丢掉最高位,保留剩下的。
        return Integer.parseInt(registerStartAddress.toString().substring(1));
    }

    /**
     * 如果previousErrorCmdReq不是空的话，代表上次指令超时，有可能乱序，则进入乱系处理逻辑；<br/>
     * 地址从0开始
     *
     * @param preErrorCmdReq 错误的指令对象
     * @param reqCmd 当前的指令对象
     * @return int
     * @author Mr.Jia
     * @date 2021/12/16 10:57
     */
    public static int getRequestRegisterStartAddress(IotReadCmdReq4Modbus preErrorCmdReq, IotReadCmdReq4Modbus reqCmd) {
        // 如果previousErrorCmdReq不是空的话，代表上次指令超时或者是解析成功的重试指令，有可能乱序，则进入乱序处理逻辑；
        if (null != preErrorCmdReq) {
            reqCmd.resetExtra();
            reqCmd.changeRequestRegisterNumberIf(preErrorCmdReq.calculateRequestRegisterNumber());
        }
        return reqCmd.calculateRequestRegisterStartAddress();
    }
}
