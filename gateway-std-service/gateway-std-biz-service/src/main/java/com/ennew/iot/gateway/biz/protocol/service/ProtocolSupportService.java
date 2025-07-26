package com.ennew.iot.gateway.biz.protocol.service;

import com.ennew.iot.gateway.core.bo.*;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import java.util.List;

public interface ProtocolSupportService {

    boolean save(ProtocolSupportBo bo);

    boolean update(ProtocolSupportBo bo);

    ProtocolSupportResBo getById(String id);

    List<ProtocolSupportResBo> query(ProtocolSupportQueryBo query);

    PageResponse<ProtocolSupportResBo> queryPage(ProtocolSupportPageQueryBo queryPage);

    boolean delete(String id);

    boolean deploy(String id);

    boolean unDeploy(String id);

    String runProtocolScript(String inputMessage, String protocolId);

    SingleResponse<String> saveReplica(ProtocolReplicaBo protocolReplicaBo);

    boolean isExistName(String name);

}
