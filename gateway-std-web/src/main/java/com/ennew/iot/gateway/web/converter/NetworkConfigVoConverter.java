package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.NetworkConfigQueryBo;
import com.ennew.iot.gateway.core.bo.NetworkConfigResBo;
import com.ennew.iot.gateway.web.vo.NetworkConfigQueryVo;
import com.ennew.iot.gateway.web.vo.NetworkConfigResVo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NetworkConfigVoConverter {

    List<NetworkConfigResVo> toNetworkConfigResList(List<NetworkConfigResBo> resBos);

    NetworkConfigQueryBo fromNetworkConfigQuery(NetworkConfigQueryVo query);
}
