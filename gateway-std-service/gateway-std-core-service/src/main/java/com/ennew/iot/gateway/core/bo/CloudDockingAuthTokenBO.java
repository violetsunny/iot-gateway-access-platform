package com.ennew.iot.gateway.core.bo;

import lombok.*;

import java.util.Map;

/**
 * @Author: alec
 * Description: 返回认证数据
 * @date: 下午1:58 2023/5/25
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CloudDockingAuthTokenBO {


    /**
     * 创建时间
     * */
    private Long createTime;

    /**
     * 过期时间
     * */
    private Long expirationTime;

    /**
     * token 方式
     * */
    private Map<String, String> tokenMap;


    private String paramsType;
}
