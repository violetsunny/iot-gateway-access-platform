package com.ennew.iot.gateway.client.message.codec;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 例子：
 * {"a":"1","b":2}
 * [{id:x ,pathValue:$.a},{id:y ,pathValue:$.b}]
 * <p>
 * {"a":"1","b":{"c":1}}
 * [{id:x ,pathValue:$.a},{id:y ,pathValue:$.b.c}]
 * <p>
 * {"a":"1","b":{"c":1},"d":[{"e":2},{"f":e}]}
 * [{id:x ,pathValue:$.a},{id:y ,pathValue:$.b.c},{id:z ,isList:true,pathValue:$.d, parameters:[{id:z1,pathValue:$.e},{id:z2,pathValue:$.f}]}]
 */
@Data
public class MetadataMapping implements Serializable {
    /**
     * 属性
     */
    private String code;
    /**
     * 属性名
     */
    private String name;
    /**
     * 获取路径
     */
    private String pathValue;
    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 是否是数组
     */
    private Integer isList;

    /**
     * 如果是数组，子数据获取
     */
    private List<MetadataMapping> parameters;

}
