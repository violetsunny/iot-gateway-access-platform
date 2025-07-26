package com.enn.iot.dtu.integration.constant;

public interface Constants {
    int MAX_RETRY_COUNT = 3;
    String IOT_SERVICE_SUCCESS_CODE = "10000";

    String IOT_SERVICE_SUCCESS_DATA_NULL_CODE = "10004";

    String GATEWAY_SN_PATTERN = "^[0-9A-Za-z_]{6,23}$";
    int RETRY_INTERVAL_TIMES = 1000;
}
