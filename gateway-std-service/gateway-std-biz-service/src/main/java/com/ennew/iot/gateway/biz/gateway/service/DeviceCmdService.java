package com.ennew.iot.gateway.biz.gateway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ennew.iot.gateway.client.protocol.model.OperationResponse;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdAckEntity;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

public interface DeviceCmdService {

    Boolean sendCmdSet(ControlCmdSetRequestBO request);

    Boolean sendCmdService(ControlCmdServiceRequestBO cmdServiceRequestBO);

    PageResponse<ControlCmdHistoryBO> queryHistoryCmdByDev(ControlCmdHistoryRequestBO request);

    PageResponse<ControlCmdAckBO> queryCmdAck(ControlCmdAckListRequestBO request);

    Boolean updateCmdStatus(String msgId, String deviceId, String status);


    void updateDownCmdRecord(OperationResponse operationResponse);


    void saveDownCmdAck(OperationResponse operationResponse, EnnDownCmdRecordEntity record);

    Boolean cmdResp(OperationResponse operationResponse);
}
