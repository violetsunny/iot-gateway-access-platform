package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: alec
 * Description: 返回数据
 * @date: 下午4:46 2023/5/29
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="cloud_docking_auth_resp", autoResultMap = true)
public class CloudDockingRespEntity {

    @TableId(value = "id")
    private String id;

    /**
     * 依赖ID
     * */
    private String hostId;

    /**
     * 获取token的key
     * */
    private String accessKey;

    /**
     * json中token 的key
     * */
    private String accessRef;

    /**
     * token 方式
     * */
    private String paramsType;

    /**
     * 获取token的前缀
     * */
    private String accessPrefix;


    /**
     * 过期时间
     * */
    private Long expireTime;


    /**
     * 过期时间类型， con-常量，ref-变量
     * */
    private String expireType;

    /**
     * 过期时间变量key
     * */
    private String expireKey;
}
