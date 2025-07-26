package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: alec
 * Description:
 * @date: 下午3:45 2023/7/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="cloud_docking_auth", autoResultMap = true)
public class CloudDockingAuthEntity {

    @TableId(value = "id")
    private String id;

    private String hostId;

    private String requestUrl;

    private String requestMethod;

    private String requestType;

    private String rootPath;
}
