package com.ennew.iot.gateway.biz.clouddocking.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ennew.iot.gateway.biz.clouddocking.manage.CloudDockingAuthManage;
import com.ennew.iot.gateway.biz.clouddocking.service.CloudDockingService;
import com.ennew.iot.gateway.client.enums.CloudDockingParamsType;
import com.ennew.iot.gateway.client.enums.CloudDockingType;
import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import com.ennew.iot.gateway.client.utils.StringUtil;
import com.ennew.iot.gateway.core.bo.*;
import com.ennew.iot.gateway.core.converter.CloudDockingBoConverter;
import com.ennew.iot.gateway.core.repository.*;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import io.vertx.core.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:37 2023/5/22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CloudDockingServiceImpl implements CloudDockingService {

    private final CloudDockingRepository cloudDockingRepository;

    private final CloudDockingAuthRepository cloudDockingAuthRepository;

    private final CloudDockingDataRepository cloudDockingDataRepository;

    private final CloudDockingParamsRepository cloudDockingParamsRepository;

    private final CloudDockingRespRepository cloudDockingRespRepository;

    private final CloudDockingMetadataRepository cloudDockingMetadataRepository;

    private final RedisService redisService;

    private final CloudDockingAuthManage cloudDockingAuthManage;

    private final CloudDockingBoConverter cloudDockingBoConverter;

    private static final String TOKEN_PRE = "CloudDockingAuth:";


    @Override
    public boolean saveCloudDocking(CloudDockingBO cloudDockingBO) {
        return cloudDockingRepository.saveCloudDocking(cloudDockingBO);
    }

    @Override
    public PageResponse<CloudDockingResBO> page(CloudDockingPageQueryBo pageQueryBo) {
        return cloudDockingRepository.queryPage(pageQueryBo);
    }

    @Override
    public boolean startup(String id) {
        return cloudDockingRepository.updateState(id, NetworkConfigState.enabled);
    }

    @Override
    public boolean shutdown(String id) {
        return cloudDockingRepository.updateState(id, NetworkConfigState.paused);
    }

    /**
     * 配置认证基础信息
     */
    @Override
    public boolean configAuthInfo(CloudDockingAuthBO cloudDockingAuthBO) {
        return cloudDockingAuthRepository.save(cloudDockingAuthBO);
    }

    /**
     * 配置认证返回数据
     */
    @Override
    public boolean configAuthRes(CloudDockingAuthResBO cloudDockingAuthBO) {
        return cloudDockingRespRepository.saveRes(cloudDockingAuthBO);
    }

    /**
     * 配置认证参数
     */
    @Override
    public boolean configAuthParams(String hostId, String type, String prodId, List<CloudDockingAuthParamsBO> cloudDockingAuthParams) {
        cloudDockingAuthParams.forEach(res -> {
            res.setHostId(hostId);
            res.setType(type);
            res.setProdId(prodId);
        });
        return cloudDockingParamsRepository.batchSaveEntity(hostId, prodId, type, cloudDockingAuthParams);
    }


    /**
     * 保存认证数据
     */
    @Override
    public boolean configDataInfo(CloudDockingDataBO cloudDockingDataBO) {
        return cloudDockingDataRepository.save(cloudDockingDataBO);
    }

    @Override
    public CloudDockingAuthTokenBO getAuthToken(String authCode) {
        log.info("获取认证器{} 认证参数", authCode);
        //从缓存获取token
        String key = String.format("%s%s", TOKEN_PRE, authCode);
        Object tokenObject = redisService.getValue(key);
        CloudDockingAuthTokenBO authToken = null;
        if (Objects.nonNull(tokenObject)) {
            try {
                //在redis中直接返回
                return JSONUtil.toBean(JSONUtil.toJsonStr(tokenObject), CloudDockingAuthTokenBO.class);
//                if (System.currentTimeMillis() - authToken.getCreateTime() > authToken.getExpirationTime()) {
//                    authToken = createAuthToken(authCode);
//                }
            } catch (Exception e) {
                log.error("转换tokenBo error", e);
                authToken = createAuthToken(authCode);
            }
        } else {
            //不在redis中
            authToken = createAuthToken(authCode);
        }
        //TODO 再存入Redis  时间都要改成秒
        redisService.putValueDuration(key, authToken, Duration.ofSeconds(authToken.getExpirationTime()));
        return authToken;
    }

    @Override
    public CloudDockingAuthTokenBO createAuthToken(String authCode) {
        log.info("认证器{} 生成认证参数", authCode);
        CloudDockingResBO cloudDockingResBO = cloudDockingRepository.getByCode(authCode);
        CloudDockingAuthBO cloudDockingAuthBO = cloudDockingAuthRepository.searchOneByHostId(authCode);
        List<CloudDockingAuthParamsBO> dockingAuthParamsList = cloudDockingParamsRepository.searchByCode(authCode, null, CloudDockingType.AUTH.getCode(), null);
        CloudDockingAuthResBO authRes = cloudDockingRespRepository.getCloudDockingAuthResBO(authCode);
        log.info("CloudDockingAuthTokenBO auth {} - {}", cloudDockingAuthBO, dockingAuthParamsList);
        JSONObject response = null;
        if (cloudDockingAuthBO.getRequestMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            response = cloudDockingAuthManage.sendGetRequest(cloudDockingResBO, cloudDockingAuthBO, dockingAuthParamsList);
        }
        if (cloudDockingAuthBO.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
            response = cloudDockingAuthManage.sendPostRequest(cloudDockingResBO, cloudDockingAuthBO, dockingAuthParamsList);
        }
        if (Objects.isNull(response)) {
            throw new BizException(ErrorCode.BIZ_ERROR);
        }

        String accessToken = response.getStr(authRes.getAccessRef());
        Map<String, String> tokenMap = new HashMap<>();
        if (authRes.getExpireType().equalsIgnoreCase("REF") && StringUtils.hasText(response.getStr(authRes.getAccessPrefix()))) {
            tokenMap.put(authRes.getAccessKey(), response.getStr(authRes.getAccessPrefix()) + " " + accessToken);
        } else {
            tokenMap.put(authRes.getAccessKey(), authRes.getAccessPrefix() + " " + accessToken);
        }

        CloudDockingAuthTokenBO authToken = CloudDockingAuthTokenBO.builder().createTime(System.currentTimeMillis())
                .tokenMap(tokenMap).paramsType(authRes.getParamsType())
                .build();
        //这个expirationTime必须要是秒，不然存redis会有错误
        if (authRes.getExpireType().equalsIgnoreCase("REF") && response.get(authRes.getExpireKey()) != null) {
            Integer expirationTime = response.getInt(authRes.getExpireKey());
            Long expirationTime2 = expirationTime == null ? response.getLong(authRes.getExpireKey()) : expirationTime.longValue();
            authToken.setExpirationTime(expirationTime2);
        } else {
            authToken.setExpirationTime(authRes.getExpireTime());
        }
        log.info("response {}", authToken);
        return authToken;
    }


    @Override
    public List<CloudDockingBodyBO> createHttpRequest(String code) {
        List<CloudDockingBodyBO> boList = new ArrayList<>();
        //获取token 认证
        CloudDockingAuthTokenBO authToken = getAuthToken(code);
        CloudDockingResBO cloudDockingResBO = cloudDockingRepository.getByCode(code);
        List<CloudDockingDataBO> cloudDockingDataBOs = cloudDockingDataRepository.searchOneByHostId(code);
        cloudDockingDataBOs.forEach(cloudDockingDataBO -> {
            String url = String.format("%s%s", cloudDockingResBO.getBaseUrl(), cloudDockingDataBO.getRequestUrl());
            List<MetadataMapping> metadataMappings = cloudDockingMetadataRepository.getMetadataMapping(code, cloudDockingDataBO.getDataCode());
            List<CloudDockingAuthParamsBO> dockingAuthParamsList = cloudDockingParamsRepository.searchByCode(code, cloudDockingDataBO.getDataCode(), CloudDockingType.PULL_DATA.getCode(), null);

            Map<String, String> headerMap = dockingAuthParamsList.stream()
                    .filter(res -> res.getParamType().equalsIgnoreCase(CloudDockingParamsType.HEADER.getCode())).collect(Collectors.toMap(CloudDockingAuthParamsBO::getParamKey, CloudDockingAuthParamsBO::getParamValue));

            if (cloudDockingDataBO.getRequestType().equalsIgnoreCase(CloudDockingType.RequestType.FORM.getCode())) {
                headerMap.put("contentType", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            } else {
                headerMap.put("contentType", MediaType.APPLICATION_JSON_VALUE);
            }

            Map<String, List<CloudDockingAuthParamsBO>> cloudDockingAuthParamsGroup = dockingAuthParamsList.stream().collect(Collectors.groupingBy(CloudDockingAuthParamsBO::getReqGroup));
            cloudDockingAuthParamsGroup.entrySet().forEach(entry -> {
                Map<String, Object> body = entry.getValue().stream()
                        .filter(res -> !res.getParamType().equalsIgnoreCase(CloudDockingParamsType.HEADER.getCode()))
                        .collect(Collectors.toMap(CloudDockingAuthParamsBO::getParamKey, res -> {
                            log.info("CloudDockingAuthParams {}", res);
                            if (res.getParamType().equalsIgnoreCase(CloudDockingParamsType.BODY.getCode())) {
                                if (res.getParamValue().startsWith("{")) {
                                    return JSONUtil.parse(res.getParamValue());
                                } else if (res.getParamValue().startsWith("[")) {
                                    return JSONUtil.parseArray(res.getParamValue());
                                }
                            }
                            return res.getParamValue();
                        }));

                String urlFinal = url;
                urlFinal = StringUtil.replace(urlFinal, body);
                if (authToken.getParamsType().equalsIgnoreCase(CloudDockingParamsType.HEADER.getCode())) {
                    headerMap.putAll(authToken.getTokenMap());
                }
                CloudDockingBodyBO bodyBO = CloudDockingBodyBO.builder().url(urlFinal)
                        .method(cloudDockingDataBO.getRequestMethod())
                        .requestType(cloudDockingDataBO.getRequestType())
                        .header(headerMap).body(body).limit(Optional.ofNullable(cloudDockingDataBO.getReqLimit()).orElse(-1))
                        .rootPath(cloudDockingDataBO.getRootPath())
                        .build();

                bodyBO.setMetadataMapping(metadataMappings);

                bodyBO.setGroup(cloudDockingDataBO.getDataCode());

                boList.add(bodyBO);
            });

        });

        return boList;
    }

    @Override
    public CloudDockingResBO getCloudDockingResBO(String code) {
        return cloudDockingRepository.getCloudDockingBO(code);
    }

    @Override
    public CloudDockingAuthBO getCloudDockingAuthBO(String code) {
        return cloudDockingAuthRepository.searchOneByHostId(code);
    }

    @Override
    public CloudDockingAuthResBO getCloudDockingAuthResBO(String code) {
        return cloudDockingRespRepository.getCloudDockingAuthResBO(code);
    }

    @Override
    public List<CloudDockingDataBO> getCloudDockingDataBO(String code) {
        return cloudDockingDataRepository.searchOneByHostId(code);
    }

    @Override
    public List<CloudDockingAuthParamsBO> cloudDockingAuthParams(String code) {
        return cloudDockingParamsRepository.searchByCode(code, null, CloudDockingType.AUTH.getCode(), null);
    }

    @Override
    public List<CloudDockingAuthParamsBO> cloudDockingDataParams(String code, String dataCode, String prodId) {
        if (!StringUtils.hasText(prodId)) {
            return new ArrayList<>();
        }
        return cloudDockingParamsRepository.searchByCode(code, dataCode, CloudDockingType.PULL_DATA.getCode(), prodId);
    }

    @Override
    public void deleteById(String id) {
        cloudDockingRepository.removeById(id);
        cloudDockingAuthRepository.removeByHost(id);
        cloudDockingParamsRepository.removeByHost(id);
    }

    @Override
    public JSONObject mockConfig(String id, String type) {
        /**
         * 查询到配置信息
         * 组装参数
         * 发送请求
         * 返回响应
         * */
        //构建auth请求数据
        CloudDockingAuthTokenBO tokenBO = createAuthToken(id);
        if (type.equalsIgnoreCase(CloudDockingType.AUTH.getCode())) {
            return JSONUtil.parseObj(tokenBO);
        }

        //发送请求
//        CloudDockingDataBO cloudDockingDataBO = getCloudDockingDataBO(id);
//        List<CloudDockingAuthParamsBO> dataRequest = cloudDockingParamsRepository.searchByCode(id, CloudDockingType.PULL_DATA.getCode(), null);
        //TODO 万明没写完
        return null;
    }


//    public CloudDockingAuthTokenBO createAuthToken(CloudDockingResBO dockingBO, CloudDockingAuthBO authBO, List<CloudDockingAuthParamsBO> authRequest) {
//        log.info("认证器{} 生成认证参数", authBO);
//        JSONObject response = null;
//        authBO.setRequestUrl(String.format("%s%s", dockingBO.getBaseUrl(), authBO.getRequestUrl()));
//        if (authBO.getRequestMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
//            response = cloudDockingAuthManage.sendGetRequest(dockingBO, authBO, authRequest);
//        }
//        if (authBO.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
//            response = cloudDockingAuthManage.sendPostRequest(dockingBO, authBO, authRequest);
//        }
//        if (Objects.isNull(response)) {
//            throw new BizException(ErrorCode.BIZ_ERROR);
//        }
//
//        String accessToken = response.getStr(authBO.getTokenKey());
//        Map<String, String> tokenMap = new HashMap<>();
//        tokenMap.put(authBO.getAccessKey(), authBO.getAccessPrefix() + accessToken);
//        CloudDockingAuthTokenBO authToken = CloudDockingAuthTokenBO.builder().createTime(System.currentTimeMillis())
//                .tokenMap(tokenMap).build();
//        authToken.setExpirationTime(authBO.getExpireTime());
//        log.info("response {}", authToken);
//        return authToken;
//    }

    @Override
    public Boolean deviceMapping(Map<String, String> params) {
        String tenant = params.get("tenant");
        params.forEach((key, value) -> {
            if (!key.equals("tenant")) {
                redisService.putValue("deviceIdSnMapping:" + tenant + ":" + key, value);
            }
        });

        return true;
    }
}
