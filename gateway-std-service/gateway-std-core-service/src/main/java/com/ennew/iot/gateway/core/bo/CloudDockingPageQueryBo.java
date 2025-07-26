package com.ennew.iot.gateway.core.bo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.*;
import top.kdla.framework.dto.PageQuery;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:12 2023/7/11
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingPageQueryBo  extends PageQuery  {

    private String name;

    private String code;

    private NetworkConfigState state;
}
