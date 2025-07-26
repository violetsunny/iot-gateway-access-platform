package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.ProtocolSupportBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportPageQueryBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportQueryBo;
import com.ennew.iot.gateway.core.bo.ProtocolSupportResBo;
import com.ennew.iot.gateway.web.vo.ProtocolSupportCmdVo;
import com.ennew.iot.gateway.web.vo.ProtocolSupportPageQueryVo;
import com.ennew.iot.gateway.web.vo.ProtocolSupportQueryVo;
import com.ennew.iot.gateway.web.vo.ProtocolSupportResVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProtocolSupportVoConverter {
    ProtocolSupportBo fromProtocolSupport(ProtocolSupportCmdVo cmd);

    ProtocolSupportPageQueryBo fromProtocolSupportPageQuery(ProtocolSupportPageQueryVo pageQuery);

    ProtocolSupportResVo toProtocolSupportRes(ProtocolSupportResBo bo);

    List<ProtocolSupportResVo> toProtocolSupportResList(List<ProtocolSupportResBo> resBos);

    ProtocolSupportQueryBo fromProtocolSupportQuery(ProtocolSupportQueryVo query);
}
