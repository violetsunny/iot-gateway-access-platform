package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongguo
 */
@Data
public class KeyValueBo<T> implements Serializable {

    private T key;

    private String value;

}
