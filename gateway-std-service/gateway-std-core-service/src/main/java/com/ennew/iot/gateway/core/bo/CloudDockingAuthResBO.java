package com.ennew.iot.gateway.core.bo;

import lombok.*;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:37 2023/5/30
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudDockingAuthResBO {

    private String hostId;
    /**
     * token key
     * */
    private String accessKey;

    /**
     * token 映射key
     * */
    private String accessRef;

    /**
     * 获取token的前缀
     * */
    private String accessPrefix;

    /**
     * token 方式
     * */
    private String paramsType;

    /**
     * 过期时间
     * */
    private Long expireTime;


    /**
     * 过期时间类型， 常量，变量
     * */
    private String expireType;

    /**
     * 过期时间变量key
     * */
    private String expireKey;
}
