package com.ennew.iot.gateway.core.bo;

import lombok.*;

/**
 * @Author: alec
 * Description: 请求体
 * @date: 下午1:37 2023/5/23
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudDockingAuthBO {

    private String hostId;

    private String requestUrl;

    private String requestMethod;

    private String requestType;

    private String rootPath;

}
