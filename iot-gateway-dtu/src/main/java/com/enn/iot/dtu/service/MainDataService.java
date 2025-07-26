package com.enn.iot.dtu.service;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.dto.ControlCmdDTO;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface MainDataService {

    /**
     * 认证Handler调用，根据认证报文查询网关标识
     *
     * @param registerPackage
     * @return
     * @throws Exception
     */
    String getGatewaySnByRegisterPackage(String registerPackage) throws Exception;

    /**
     * 认证成功后加载档案配置调动
     *
     * @param ctx
     *            ChannelHandlerContext
     * @return 是否更新了配置
     * @throws Exception
     */
    boolean refreshMainDataIf(ChannelHandlerContext ctx) throws Exception;

    /**
     * 认证成功后加载档案配置调动
     *
     * @param gatewaySn
     * @return 是否更新了配置
     * @throws Exception
     */
    boolean refreshMainDataIf(String gatewaySn) throws Exception;

    /**
     * 检查并刷新DTU的档案信息。针对的是已连接当前节点的DTU。 Job 作业调用
     *
     * @throws Exception
     */
    void refreshAllMainDataIf() throws Exception;

    /**
     * 生成写指令列表
     *
     * @param controlCmdDTO 指令对象
     * @return java.util.List<com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq>
     * @author Mr.Jia
     * @date 2022/7/23 10:22 AM
     */
    List<AbstractIotCmdReq> generateWriteCmdListByProtocol(ControlCmdDTO controlCmdDTO);
}
