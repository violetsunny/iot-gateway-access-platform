package com.ennew.iot.gateway.core.bo;

import lombok.Data;
import top.kdla.framework.dto.PageQuery;

@Data
public class TrdPlatformTaskPageQueryBo extends PageQuery {

    /**
     * 平台code
     */
    private String pCode;

    /**
     * 任务名称
     */
    private String taskName;

}
