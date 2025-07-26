package com.ennew.iot.gateway.core.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.common.constants.RedisConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RedisTemplate<String, Object> getTemplate() {
        return redisTemplate;
    }

    public void putValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void putValueDuration(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Map<Object, Object> getHashEntries(String hashName) {
        return redisTemplate.opsForHash().entries(hashName);
    }

    public Map<Object, Object> getHashDevice(String deviceId) {
        return getHashEntries("device:" + deviceId);
    }

    public Object getHash(String hashName, Object hashKey) {
        return redisTemplate.opsForHash().get(hashName, hashKey);
    }

    public void putHash(String hashName, String hashKey, Object hashValue) {
        redisTemplate.opsForHash().put(hashName, hashKey, hashValue);
    }

    public void deleteHash(String hashName, String hashKey) {
        redisTemplate.opsForHash().delete(hashName, hashKey);
    }

    public String getDeviceIdHashFromRedis(String deviceId, String hashKey) {
        return (String) redisTemplate.opsForHash().get("device:" + deviceId, hashKey);
    }

    public boolean putDeviceIdHashToRedis(String deviceId, String hashName, String hashValue) {
        return redisTemplate.opsForHash().putIfAbsent("device:" + deviceId, hashName, hashValue);
    }

    public void publish(String topic, Object msg) {
        redisTemplate.convertAndSend(topic, msg);
    }

    public Long hashIncrement(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    public String getDeviceTypeFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.DEVICE_TYPE);
    }

    public Integer getTestFlagFromRedis(String deviceId) {
        return (Integer) redisTemplate.opsForHash().get("device:" + deviceId, RedisConstant.TEST_FLAG);
    }

    public String getTenantIdFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.TENANT_ID);
    }

    public String getProductIdIdFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.PRODUCT_ID);
    }

    public String getDomainFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.DOMAIN);
    }

    public String getStaIdFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.PROJECT_CODE);
    }

    public String getDeviceCodeFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.THIRD_CODE);
    }

    public String getGatewayFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.IS_GATEWAY_DEVICE);
    }

    public String getSateFromRedis(String deviceId) {
        return getDeviceIdHashFromRedis(deviceId, RedisConstant.STATE);
    }

    public JSONArray getMeasurePsFromRedisByProduct(String productId) {
        Object object = redisTemplate.opsForHash().get("device-product:" + productId, "metadata");
        if (object == null) {
            return null;
        }
        String jsonStr = object.toString();
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        if (jsonObject == null) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("measureProperties");
        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        return jsonArray;
    }

    public Map<String, Object> getDeviceAttrsFromRedis(String deviceId) {
        List<Object> fieldList = Arrays.asList(
                RedisConstant.TEST_FLAG,         //填充debug
                RedisConstant.TENANT_ID,         //原名填充 租户id
                RedisConstant.ENTITY_TYPE_CODE,   //填充devType 物模型标识
                RedisConstant.PRODUCT_ID,        //原名填充 产品id
                RedisConstant.PERIOD,           //原名填充 频率
                RedisConstant.DEVICE_NAME,       //原名填充 设备名称
                RedisConstant.SN,               //原名填充 设备标识
                RedisConstant.DEPT_ID,           //原名填充 企业id
                RedisConstant.TAGS,
                RedisConstant.DEVICE_TYPE,       //原名填充 设备类型：子设备、网关、DTU等
                RedisConstant.ENTITY_TYPE_NAME,   //原名填充 物模型名称
                RedisConstant.AREA_CODE,          //填充county 地区编码
                RedisConstant.PARENT_ID,         //原名填充 父设备
                RedisConstant.THIRD_CODE,        //填充deviceCode 设备编码
                RedisConstant.DOMAIN,           //原名填充 业务域
                RedisConstant.PROJECT_CODE,      //填充staId 场站id
                RedisConstant.SOURCE           //原名填充 数据源
        );
        List<Object> valueList = redisTemplate.opsForHash().multiGet("device:" + deviceId, fieldList);
        if (CollectionUtils.isEmpty(valueList.stream().filter(Objects::nonNull).collect(Collectors.toList()))) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < fieldList.size(); i++) {
            map.put((String) fieldList.get(i), valueList.get(i));
        }
        return map;
    }

    public List<Object> getNewMultiAttrsFromRedis(String deviceId) {
        // 指定要获取的多个 Hash 的字段
        List<Object> fieldList = Arrays.asList("testFlag", "tenantId", "entityTypeCode", "productId", "period", "deviceName", "sn", "deptId", "tags", "deviceType", "entityTypeName", "areaCode", "parentId", "thirdCode", "domain", "projectCode");
        // 调用 multiGet 方法一次获取多个 Hash 的值
        return redisTemplate.opsForHash().multiGet("device:" + deviceId, fieldList);
    }

    public String getThirdCloudDeviceId(String sn) {
        return stringRedisTemplate.opsForValue().get("thirdCloud:" + sn);
    }

    public List<Object> getThirdCloudProductId(String productId) {
        //上行/下行标识 (up/down)
        return redisTemplate.opsForHash().multiGet("thirdCloud:product:" + productId, Arrays.asList("up_message_protocol_id", "down_message_protocol_id", "master_apikey"));
    }


    public String getParentIdFromRedis(String deviceId) {
        String parenId = (String) redisTemplate.opsForHash().get("device:" + deviceId, "parentId");
        if (parenId == null) {
            parenId = (String) redisTemplate.opsForHash().get("device:" + deviceId, "parentGatewayId");
        }
        return parenId;
    }


    public String getProductIdFromRedis(String deviceId) {
        return (String) redisTemplate.opsForHash().get("device:" + deviceId, "productId");
    }

    public String getProductCodeFromRedis(String productId) {
        return (String) redisTemplate.opsForHash().get("device-product:" + productId, "code");
    }

    public String getSnFromRedis(String deviceId) {
        return (String) redisTemplate.opsForHash().get("device:" + deviceId, "sn");

    }
}
