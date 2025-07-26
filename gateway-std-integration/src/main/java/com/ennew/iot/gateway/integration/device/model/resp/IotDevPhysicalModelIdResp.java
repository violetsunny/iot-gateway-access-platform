package com.ennew.iot.gateway.integration.device.model.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ruanhong
 */
@Data
public class IotDevPhysicalModelIdResp<T> implements Serializable {

    private boolean success;
    private Integer code;
    private String msg;
    private T data;

}
