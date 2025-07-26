package com.ennew.iot.gateway.integration.device.model;

import lombok.Data;

import java.util.List;


@Data
public class DeviceDataBatchReq {

    private List<String> deviceIds;


    private boolean tagQueryFlag;
}
