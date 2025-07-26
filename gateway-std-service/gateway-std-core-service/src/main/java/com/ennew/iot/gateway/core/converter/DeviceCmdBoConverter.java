package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.core.bo.ControlCmdAckBO;
import com.ennew.iot.gateway.core.bo.ControlCmdHistoryBO;
import com.ennew.iot.gateway.core.bo.DeviceGatewayBo;
import com.ennew.iot.gateway.core.bo.DeviceGatewayResBo;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdAckEntity;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceCmdBoConverter {

    List<ControlCmdHistoryBO> toCmdRecordRes(List<EnnDownCmdRecordEntity> records);
    List<ControlCmdAckBO> toCmdAckRes(List<EnnDownCmdAckEntity> records);

}
