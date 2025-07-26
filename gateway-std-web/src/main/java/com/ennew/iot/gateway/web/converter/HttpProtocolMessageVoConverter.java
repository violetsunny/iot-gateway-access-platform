package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.HttpEventDataBo;
import com.ennew.iot.gateway.core.bo.HttpGatewayStatusBo;
import com.ennew.iot.gateway.core.bo.HttpProtocolMessageBO;
import com.ennew.iot.gateway.web.vo.HttpEventDataCmd;
import com.ennew.iot.gateway.web.vo.HttpGatewayRtgDataCmd;
import com.ennew.iot.gateway.web.vo.HttpGatewayStatusCmd;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:24 2023/4/27
 */
@Mapper(componentModel = "spring")
public interface HttpProtocolMessageVoConverter {

    /**
     * VO 转 BO
     * @param dataCmd VO
     * @return BO
     * */
    @Mappings({})
    HttpProtocolMessageBO toMessageBodyBO(HttpGatewayRtgDataCmd dataCmd);


    /**
     * VO 转 BO
     * @param dataCmd VO
     * @return BO
     * */
    List<HttpProtocolMessageBO> toMessageBodyBO(List<HttpGatewayRtgDataCmd> dataCmd);

    HttpEventDataBo toEventRequestBO(HttpEventDataCmd httpEventDataCmd);

    HttpGatewayStatusBo toStatusRequestBO(HttpGatewayStatusCmd httpGatewayStatusCmd);

}
