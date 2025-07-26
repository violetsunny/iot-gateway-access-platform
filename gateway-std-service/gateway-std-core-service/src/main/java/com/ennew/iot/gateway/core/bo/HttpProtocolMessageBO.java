package com.ennew.iot.gateway.core.bo;

import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:19 2023/4/27
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HttpProtocolMessageBO {

    private String dev;

    private Long ts;

    private String resume = "N"; //Y-续传，N-非续传

    private List<RtgData> d;

    public HttpProtocolMessageBO(String dev, Long ts, Map<String, Object> map) {
        this.dev = dev;
        this.ts = ts;
        this.d = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            d.add(new RtgData(entry.getKey(), entry.getValue()));
        }
    }

    public Map<String, Object> getMetric() {
        if (CollectionUtils.isEmpty(d)) {
            return new HashMap<>(0);
        }
        return d.stream().filter(rtgData -> rtgData.getM() != null && rtgData.getV() != null).collect(Collectors.toMap(RtgData::getM, RtgData::getV, (v1, v2) -> v1));
    }


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class RtgData {

        private String m;

        private Object v;
    }
}
