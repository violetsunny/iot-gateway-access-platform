package com.ennew.iot.gateway.core.bo;

import lombok.*;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:37 2023/5/30
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CloudDockingMetaBO {

    private String name;


    private String pathValue;
    /**
     * 数据类型
     * */
    private String dataType;

    /**
     * 是否数组
     * */
    private Boolean isList;

    /**
     * 子数据
     * */
    private List<CloudDockingMetaBO> parameters;
}
