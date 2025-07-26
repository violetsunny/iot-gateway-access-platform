package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.web.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:14 2023/7/11
 */
@Mapper(componentModel = "spring")
public interface DeviceCmdVoConverter {


    ControlCmdSetRequestBO fromControlCmdSetRequest(ControlCmdSetRequestVo request);

    ControlCmdServiceRequestBO fromControlCmdServiceRequest(ControlCmdServiceRequestVo request);

    ControlCmdHistoryRequestBO fromHistoryCmd(ControlCmdHistoryRequestVo request);

    ControlCmdAckListRequestBO fromCmdAck(ControlCmdAckListRequestVo request);
}
