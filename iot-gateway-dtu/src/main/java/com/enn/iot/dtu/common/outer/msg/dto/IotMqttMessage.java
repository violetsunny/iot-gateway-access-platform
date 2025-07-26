package com.enn.iot.dtu.common.outer.msg.dto;

import com.enn.iot.dtu.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class IotMqttMessage {

    @JsonProperty("ver")
    private String version = "2.2.8";

    @JsonProperty("pKey")
    private String productKey;

    @JsonProperty("sn")
    private String serialNumber;

    @JsonProperty("ts")
    private Long timestamp;

    @JsonProperty("devs")
    private List<IotMqttDevice> deviceList;

    public void setGatewaySn(String gatewaySn) throws ArrayIndexOutOfBoundsException {
        String[] pkSn = gatewaySn.split("_");
        if (pkSn.length == 1) {
            this.productKey = "";
            this.serialNumber = pkSn[0];
        } else {
            this.productKey = pkSn[0];
            this.serialNumber = pkSn[1];
        }
    }

    public String toUncimMessage() {
        Map<String, Object> uncimMessage = new HashMap<>(2);
        uncimMessage.put("topic", "/edge/" + this.getProductKey() + "/" + this.getSerialNumber() + "/rtg");
        uncimMessage.put("data", JsonUtils.writeValueAsString(this));
        return JsonUtils.writeValueAsString(uncimMessage);
    }
}
