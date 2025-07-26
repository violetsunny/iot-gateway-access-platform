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
public interface CloudDockingVoConverter {

    @Mapping(target = "state", expression = "java(com.ennew.iot.gateway.dal.enums.NetworkConfigState.convert(pageQuery.getState()))")
    CloudDockingPageQueryBo fromDeviceGatewayPageQuery(CloudDockingPageQueryVo pageQuery);


    CloudDockingBO fromCloudDockingCmd(CloudDockingCmdVo cmdVo);


    CloudDockingResVo toCloudDockingRes(CloudDockingResBO cloudDockingRes);



    List<CloudDockingResVo> toCloudDockingRes(List<CloudDockingResBO> cloudDockingRes);

    CloudDockingAuthBO fromCloudDockingAuthCmd(CloudDockingAuthCmdVo cloudDockingAuthCmdVo);

    CloudDockingAuthResCmdVo fromCloudDockingAuthResBO(CloudDockingAuthResBO cloudDockingAuthBO);

    CloudDockingAuthCmdVo fromCloudDockingAuthBO(CloudDockingAuthBO cloudDockingAuthBO);

    CloudDockingDataCmdVo fromCloudDockingDataBO(CloudDockingDataBO cloudDockingDataBO);

    List<CloudDockingDataCmdVo> fromCloudDockingDataBOs(List<CloudDockingDataBO> cloudDockingDataBO);

    CloudDockingAuthResBO fromCloudDockingAuthCmd(CloudDockingAuthResCmdVo cloudDockingAuthResCmdVo);

    CloudDockingAuthParamsBO fromCloudDockingAuthParamsCmd(CloudDockingParamsCmdVo cloudDockingParamsCmdVo);

    List<CloudDockingAuthParamsBO> fromCloudDockingAuthParamsCmd(List<CloudDockingParamsCmdVo> cloudDockingParamsCmdVo);

    CloudDockingParamsCmdVo fromCloudDockingAuthBO(CloudDockingAuthParamsBO cloudDockingAuthParamsCmdVo);

    List<CloudDockingParamsCmdVo> fromCloudDockingAuthBO(List<CloudDockingAuthParamsBO> cloudDockingAuthParamsCmdVo);


    CloudDockingDataBO fromCloudDockingDataCmd(CloudDockingDataCmdVo cloudDockingDataCmdVo);
}
