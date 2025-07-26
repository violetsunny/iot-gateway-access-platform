package com.ennew.iot.gateway.biz.cloudgateway;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDeviceMetricBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayDevicePageQueryBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayDeviceEntity;
import top.kdla.framework.dto.PageResponse;

import java.util.Collection;
import java.util.List;

public interface CloudGatewayDeviceService extends IService<CloudGatewayDeviceEntity> {


    /**
     * 是否已存在绑定关系
     *
     * @param gatewayCode 网关编码
     * @param deviceIdList 设备ID集合
     * @return 已绑定deviceId集合
     */
    List<String> exists(String gatewayCode, List<String> deviceIdList);



    /**
     * 绑定设备
     *
     * @param entityList 绑定关系集合
     * @return 是否绑定成功
     */
    boolean bindDevice(String gatewayCode, Collection<CloudGatewayDeviceEntity> entityList);


    /**
     * 解绑设备
     *
     * @param gatewayCode 网关编码
     * @param deviceIdList 设备ID集合
     * @return 是否解绑成功
     */
    boolean unbindDevice(String gatewayCode, List<String> deviceIdList);


    /**
     * 分页查询
     *
     * @param gatewayCode 网关编码
     * @param queryBO 查询参数
     * @return 分页数据对象
     */
    PageResponse<CloudGatewayDeviceBO> queryPage(String gatewayCode, CloudGatewayDevicePageQueryBO queryBO);


    /**
     * 查询网关所有关联设备ID
     *
     * @param gatewayCode 网关编码
     * @return 设备ID集合
     */
    List<CloudGatewayDeviceBO> queryList(String gatewayCode);


    /**
     * 查询云网关已关联设备数量
     *
     * @param gatewayCode 网关编码
     * @return 已关联设备数
     */
    Long queryDeviceCount(String gatewayCode);



    List<CloudGatewayDeviceMetricBO> queryDeviceMetricList(String productId, String deviceId);

}
