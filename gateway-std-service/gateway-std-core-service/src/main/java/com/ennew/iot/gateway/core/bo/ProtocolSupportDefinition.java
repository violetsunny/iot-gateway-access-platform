package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ProtocolSupportDefinition implements Serializable {

    private String id;
    private String name;
    private String description;

    private String provider;//jar script

    private byte state;

    private Map<String,Object> configuration;

    public static ProtocolSupportDefinition toDeployDefinition(ProtocolSupportResBo bo) {
        ProtocolSupportDefinition definition = new ProtocolSupportDefinition();
        definition.setId(bo.getId());
        definition.setConfiguration(bo.getConfiguration());
        definition.setName(bo.getName());
        definition.setProvider(bo.getType());
        definition.setState((byte) 1);

        return definition;
    }

    public static ProtocolSupportDefinition toUnDeployDefinition(ProtocolSupportResBo bo) {
        ProtocolSupportDefinition definition = toDeployDefinition(bo);
        definition.setState((byte) 0);
        return definition;
    }
}
