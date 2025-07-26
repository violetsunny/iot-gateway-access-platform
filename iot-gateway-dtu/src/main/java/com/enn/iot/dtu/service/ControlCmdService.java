package com.enn.iot.dtu.service;

/**
 * 下行指令接口
 *
 * @author Mr.Jia
 * @date 2022/7/23 9:44 AM
 */
public interface ControlCmdService {

    /**
     * 加载下行指令,并执行尝试执行指令
     *
     * @param gatewaySn 网关标识
     * @author Mr.Jia
     * @date 2022/7/23 9:43 AM
     */
    void loadDtuControlCommand(String gatewaySn);

    /**
     * 查询一条控制指令并添加到高优先级指令队列里面去
     *
     * @author Mr.Jia
     * @date 2022/9/28 11:07 PM
     * @param gatewaySn 网关标识
     * @return java.lang.Boolean
     */
    Boolean insertCmdToWriteQueueByGatewaySn(String gatewaySn);
}
