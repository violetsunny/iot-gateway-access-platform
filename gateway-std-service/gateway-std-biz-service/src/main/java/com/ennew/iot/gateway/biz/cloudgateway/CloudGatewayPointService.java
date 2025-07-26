package com.ennew.iot.gateway.biz.cloudgateway;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusPointBO;
import com.ennew.iot.gateway.core.bo.ExcelImportErrorBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusPointImportBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointPageQueryBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import top.kdla.framework.dto.PageResponse;

import java.util.List;

public interface CloudGatewayPointService extends IService<CloudGatewayPointEntity> {


    /**
     * 点位是否存在
     *
     * @param point 点位信息
     * @return true 存在 false 不存在
     */
    boolean exists(CloudGatewayPointEntity point);


    /**
     * 点位是否存在
     *
     * @param pointId 点位ID
     * @return 是否存在
     */
    boolean exists(Long pointId);


    /**
     * 保存点位配置信息
     *
     * @param point 点位信息
     * @return 是否添加成功
     */
    boolean savePoint(CloudGatewayPointEntity point);


    /**
     * 批量保存点位信息
     *
     * @param gatewayCode 网关编码
     * @param points 点位信息集合
     * @return 是否保存成功
     */
    boolean savePoints(String gatewayCode, List<CloudGatewayPointEntity> points);



    ExcelImportErrorBO importModbusPoints(String gatewayCode, List<CloudGatewayModbusPointImportBO> importBOList);


    /**
     * 点位分页查询
     *
     * @param gatewayCode 网关编码
     * @param pageQueryBO 分页查询参数
     * @return 分页查询结果
     */
    PageResponse<CloudGatewayPointBO> queryPage(String gatewayCode, CloudGatewayPointPageQueryBO pageQueryBO);


    /**
     * 获取网关测点信息
     *
     * @param gatewayCode 网关编码
     * @return json
     */
    List<? extends CloudGatewayPointBO> getPoints(String gatewayCode);


    /**
     * 获取modbus网关测点
     *
     * @param gatewayCode 网关编码
     * @return modbus点位列表
     */
    List<CloudGatewayModbusPointBO> getModbusPoints(String gatewayCode);


    /**
     * 查询云网关已导入点位数量
     *
     * @param gatewayCode 网关编码
     * @return 原始点位数量
     */
    Long queryPointCount(String gatewayCode);

}
