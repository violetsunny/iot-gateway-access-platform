package com.ennew.iot.gateway.core.converter;

import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.dal.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午1:38 2023/5/23
 */
@Mapper(componentModel = "spring")
public interface CloudDockingBoConverter {

    /**
     * BO转Entity
     * @param cloudDockingBO BO
     * @return Entity
     * */
    CloudDockingEntity toCloudDockingEntity(CloudDockingBO cloudDockingBO);

    @Mapping(target = "state", expression = "java(com.ennew.iot.gateway.dal.enums.NetworkConfigState.convert(entity.getState()))")
    CloudDockingResBO toCloudDockingRes(CloudDockingEntity entity);

    List<CloudDockingResBO> toCloudDockingRes(List<CloudDockingEntity> records);

    CloudDockingAuthEntity toCloudDockingAuthEntity(CloudDockingAuthBO cloudDockingAuthBO);

    CloudDockingRespEntity toCloudDockingRespEntity(CloudDockingAuthResBO cloudDockingAuthBO);

    CloudDockingDataEntity toCloudDockingDataEntity(CloudDockingDataBO cloudDockingDataBO);

    CloudDockingParamsEntity toCloudDockingParamsEntity(CloudDockingAuthParamsBO params);

    List<CloudDockingParamsEntity> toCloudDockingParamsEntity(List<CloudDockingAuthParamsBO> params);

    CloudDockingAuthParamsBO toDockingAuthParamsBo(CloudDockingParamsEntity cloudDockingParamsEntity);

    List<CloudDockingAuthParamsBO> toDockingAuthParamsBo(List<CloudDockingParamsEntity> cloudDockingParamsEntity);

    CloudDockingAuthResBO toCloudDockingRespBo(CloudDockingRespEntity cloudDockingRespEntity);

    CloudDockingAuthBO toCloudDockingRespBo(CloudDockingAuthEntity cloudDockingAuthEntity);

    CloudDockingDataBO toCloudDockingDataBO(CloudDockingDataEntity cloudDockingDataEntity);

    List<CloudDockingDataBO> toCloudDockingDataBOs(List<CloudDockingDataEntity> cloudDockingDataEntity);

    MetadataMapping toCloudDockingMetadata(CloudDockingMetadataEntity cloudDockingMetadataEntity);

    List<MetadataMapping> toCloudDockingMetadata(List<CloudDockingMetadataEntity> cloudDockingMetadataEntity);

}
