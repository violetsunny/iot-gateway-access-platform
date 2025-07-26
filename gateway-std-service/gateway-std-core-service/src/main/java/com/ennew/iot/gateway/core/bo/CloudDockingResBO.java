package com.ennew.iot.gateway.core.bo;

import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.dal.entity.NetworkConfigEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.*;

import java.util.Date;
import java.util.Map;

/**
 * @Author: alec
 * Description:
 * @date: 下午1:54 2023/7/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingResBO {

    private String id;

    private String name;

    private String code;

    private String baseUrl;

    private NetworkConfigState state;

    private Date createTime;

}
