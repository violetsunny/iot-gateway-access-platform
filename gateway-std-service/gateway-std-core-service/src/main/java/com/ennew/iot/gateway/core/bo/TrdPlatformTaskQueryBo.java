package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrdPlatformTaskQueryBo implements Serializable {

    private String taskCode;

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 任务名称
     */
    private String taskName;

    private String productId;

}
