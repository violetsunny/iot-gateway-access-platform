package com.ennew.iot.gateway.core.bo;

import lombok.Data;

@Data
public class ProtocolSupportQueryBo {

    private String id;

    private String name;

    private String type;

    private Integer way;

    private Byte state;

    private Byte isTemplate;

    private Boolean filterUnused;

    private String include;

    private Integer isDeleted;

}
