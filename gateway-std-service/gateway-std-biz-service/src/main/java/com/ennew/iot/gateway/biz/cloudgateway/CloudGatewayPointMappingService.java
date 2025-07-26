package com.ennew.iot.gateway.biz.cloudgateway;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingPageQueryBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusMappingBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointMappingBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointMappingEntity;
import top.kdla.framework.dto.PageResponse;

import java.util.List;

public interface CloudGatewayPointMappingService extends IService<CloudGatewayPointMappingEntity> {


    /**
     * 映射关系是否存在
     *
     * @param pointMappingEntity 映射关系对象
     * @return 是否存在
     */
    boolean exists(CloudGatewayPointMappingEntity pointMappingEntity);


    /**
     * 获取网关点位与平台点位映射关系
     *
     * @param gatewayCode 网关编码
     * @return json
     */
    List<CloudGatewayPointMappingBO> getPointMapping(String gatewayCode);


    /**
     * 设备测点和原始点位映射解绑
     *
     * @param pointId 点位ID
     * @return 是否成功
     */
    boolean unbind(String gatewayCode, Long pointId);



    PageResponse<CloudGatewayModbusMappingBO> queryModbusMappingPage(String gatewayCode, CloudGatewayModbusMappingPageQueryBO queryBO);


    boolean isBind(String gatewayCode, Long pointId);
}
