package com.ennew.iot.gateway.client.protocol.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class InfoReportRequest extends Message {
    JSONObject data;
}
