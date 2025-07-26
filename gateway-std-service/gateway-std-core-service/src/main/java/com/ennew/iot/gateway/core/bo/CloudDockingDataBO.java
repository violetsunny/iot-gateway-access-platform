package com.ennew.iot.gateway.core.bo;

import lombok.*;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:58 2023/7/14
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudDockingDataBO {

    private String hostId;

    private String dataCode;

    private String requestUrl;

    private String requestType;

    private String requestMethod;

    private String rootPath;

    private Integer split;

    private Integer reqLimit;
}
