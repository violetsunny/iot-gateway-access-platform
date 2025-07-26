package com.ennew.iot.gateway.core.bo;

import com.ennew.iot.gateway.client.message.codec.MetadataMapping;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: alec
 * Description: 氢气接口报文
 * @date: 上午10:09 2023/5/26
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CloudDockingBodyBO {

    /**
     * 请求URL
     * */
    private String url;

    /**
     * 请求方法
     * */
    private String method;

    /**
     * 请求头参数
     * */
    private Map<String, String> header;

    /**
     * 请求体参数
     * */
    private Object body;

    /**
     * 请求类型， Params, Or Body
     * */
    private String requestType;

    /**
     * 限流 默认 0
     * */
    private Integer limit;

    /**
     * 返回根路径
     */
    private String rootPath;

    /**
     * 数据转换格式
     * */
    private List<MetadataMapping> metadataMapping;

    /**
     * 是否同组
     */
    private String group;
}
