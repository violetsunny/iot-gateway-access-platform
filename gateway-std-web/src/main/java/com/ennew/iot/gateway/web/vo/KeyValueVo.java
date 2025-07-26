package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dongguo
 */
@Schema(description = "属相CODE和值信息")
@Data
public class KeyValueVo<T> implements Serializable {


    @Schema(description = "属性CODE")
    private T key;

    @Schema(description = "属性值")
    private String value;

}
