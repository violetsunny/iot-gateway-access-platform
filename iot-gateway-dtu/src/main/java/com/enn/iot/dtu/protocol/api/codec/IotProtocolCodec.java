package com.enn.iot.dtu.protocol.api.codec;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.codec.dto.IotCmdResp;
import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.List;

public interface IotProtocolCodec {

    /**
     * 检测应答报文帧是否正确<br/>
     * error code
     *
     * @param respByteBuf
     * @param iotCmdReq
     * @return if return 0, then validation success; else validation failed。
     * @throws Exception
     */
    IotDetectionResult detectResponseFrame(ByteBuf respByteBuf, AbstractIotCmdReq iotCmdReq) throws Exception;

    /**
     * 把应答报文解码为应答对象
     *
     * @param respFrameByteBuf
     * @param iotCmdReq
     * @throws Exception
     */
    IotCmdResp decode(ByteBuf respFrameByteBuf, AbstractIotCmdReq iotCmdReq) throws Exception;

    /**
     * 把请求指令编码为请求报文 堆外内存: alloc.directBuffer(initialCapacity)
     *
     * @param currentCmdReq
     * @param previousErrorCmdReq
     *            如果上一条指令执行失败，则该参数不为空; 如果上一条指令执行成功，则该参数为空;
     * @param alloc
     * @return
     * @throws Exception
     */
    ByteBuf encode(AbstractIotCmdReq currentCmdReq, AbstractIotCmdReq previousErrorCmdReq, ByteBufAllocator alloc)
            throws Exception;

    /**
     * 根据档案配置，生成读指令列表。
     *
     * @param mainData
     * @return
     * @throws Exception
     */
    List<AbstractIotCmdReq> generateReadCmdListByMainData(MainDataDTO mainData) throws Exception;

    /**
     * 根据档案配置，生成写指令列表。
     *
     * @param mainData
     * @returnßßßß
     * @throws Exception
     */
    List<AbstractIotCmdReq> generateWriteCmdListByCmdData(ControlCmdDTO mainData) throws Exception;
}
