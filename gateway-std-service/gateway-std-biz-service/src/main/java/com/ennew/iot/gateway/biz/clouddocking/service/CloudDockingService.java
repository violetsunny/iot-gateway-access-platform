package com.ennew.iot.gateway.biz.clouddocking.service;

import cn.hutool.json.JSONObject;
import com.ennew.iot.gateway.core.bo.*;
import top.kdla.framework.dto.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:35 2023/5/22
 */
public interface CloudDockingService {

    /**
     * 新增三方云平台
     *
     * @param cloudDockingBO 三方云平台BO
     * @return 返回是否创建成功
     */
    boolean saveCloudDocking(CloudDockingBO cloudDockingBO);

    /**
     * 分页查询
     *
     * @param pageQueryBo 查询参数
     * @return 分页报文
     */
    PageResponse<CloudDockingResBO> page(CloudDockingPageQueryBo pageQueryBo);

    /**
     * 启用
     *
     * @param id ID
     * @return 是否成功
     */
    boolean startup(String id);

    /**
     * 禁用
     *
     * @param id ID
     * @return 是否成功
     */
    boolean shutdown(String id);

    /**
     * 配置认证信息
     *
     * @param cloudDockingAuthBO 认证信息
     * @return 是否成功
     */
    boolean configAuthInfo(CloudDockingAuthBO cloudDockingAuthBO);

    /**
     * 配置认证信息
     *
     * @param cloudDockingAuthBO 认证信息
     * @return 是否成功
     */
    boolean configAuthRes(CloudDockingAuthResBO cloudDockingAuthBO);


    /**
     * 配置认证参数
     *
     * @param cloudDockingAuthParams 参数
     * @param hostId                 id
     * @param type                   类型
     * @param prodId                 产品ID
     * @return 是否成功
     */
    boolean configAuthParams(String hostId, String type, String prodId, List<CloudDockingAuthParamsBO> cloudDockingAuthParams);


    /**
     * 配置数据信息
     *
     * @param cloudDockingDataBO 数据参数
     * @return 返回是否成功
     */
    boolean configDataInfo(CloudDockingDataBO cloudDockingDataBO);

    /**
     * 获取云接入认证token
     *
     * @param authCode 认证器编码
     * @return 返回认证参数
     */
    CloudDockingAuthTokenBO getAuthToken(String authCode);

    /**
     * 生成authToken
     *
     * @param authCode 认证器编码
     * @return 返回认证参数
     */
    CloudDockingAuthTokenBO createAuthToken(String authCode);


    /**
     * 返回请求参数
     *
     * @param code 编码
     * @return 返回请求参数
     */
    List<CloudDockingBodyBO> createHttpRequest(String code);


    /**
     * 查找详情
     *
     * @param code 编码
     * @return 返回
     */
    CloudDockingResBO getCloudDockingResBO(String code);

    /**
     * 查找详情
     *
     * @param code 编码
     * @return 返回
     */
    CloudDockingAuthBO getCloudDockingAuthBO(String code);

    /**
     * @param code
     * @return
     */
    CloudDockingAuthResBO getCloudDockingAuthResBO(String code);

    /**
     * 查找数据详情
     *
     * @param code 编码
     * @return 返回数据
     */
    List<CloudDockingDataBO> getCloudDockingDataBO(String code);

    /**
     * 查找详情
     *
     * @param code 编码
     * @return 返回
     */
    List<CloudDockingAuthParamsBO> cloudDockingAuthParams(String code);

    /**
     * 查找详情
     *
     * @param code   编码
     * @param prodId 产品ID
     * @return 返回
     */
    List<CloudDockingAuthParamsBO> cloudDockingDataParams(String code, String dataCode, String prodId);

    /**
     * 根据ID删除
     *
     * @param id id
     */
    void deleteById(String id);

    /**
     * 验证配置
     *
     * @param id   配置ID
     * @param type 配置类型
     * @return 返回请求结果
     */
    JSONObject mockConfig(String id, String type);


    Boolean deviceMapping(Map<String, String> params);

}
