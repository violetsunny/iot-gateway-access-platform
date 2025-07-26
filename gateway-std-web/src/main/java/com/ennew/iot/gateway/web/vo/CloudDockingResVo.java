package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

/**
 * @Author: alec
 * Description:
 * @date: 下午1:50 2023/7/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingResVo {

    private String id;

    private String name;

    private String code;

    private String baseUrl;

    private NetworkConfigState state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
