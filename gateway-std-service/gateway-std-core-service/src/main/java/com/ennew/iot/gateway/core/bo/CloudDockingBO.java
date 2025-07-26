package com.ennew.iot.gateway.core.bo;

import lombok.*;

/**
 * @Author: alec
 * Description:
 * @date: 上午9:57 2023/7/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingBO {

    private String name;

    private String code;

    private String baseUrl;
}
