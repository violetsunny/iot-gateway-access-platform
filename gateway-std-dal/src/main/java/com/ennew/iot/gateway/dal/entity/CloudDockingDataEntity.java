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
 * @date: 下午2:00 2023/7/20
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="cloud_docking_data", autoResultMap = true)
public class CloudDockingDataEntity {

    @TableId(value = "id")
    private String id;

    private String hostId;

    /**
     * 请求code
     */
    private String dataCode;

    private String requestUrl;

    /**
     * 请求类型
     * form, json
     * */
    private String requestType;

    private String requestMethod;

    private String rootPath;

    private Integer split;

    private Integer reqLimit;
}
