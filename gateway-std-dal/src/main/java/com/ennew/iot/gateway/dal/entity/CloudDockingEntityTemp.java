package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: alec
 * Description: 拉去
 * @date: 下午3:22 2023/5/22
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="cloud_docking", autoResultMap = true)
public class CloudDockingEntityTemp {

    @TableId(value = "id")
    private String id;


    private String code;


    /**
     * 类型  Auth, PullData, SendCmd
     * */
    private String type;

    /**
     * 请求类型
     * form, json
     * */
    private String requestType;

    /**
     * 名称
     * */
    private String name;

    /**
     * URL
     * */
    private String requestUrl;

    /**
     * 认证方式
     * */
    private String requestMethod;


    /**
     * 返回值报文根目录
     * */
    private String resBodyPrefix;

    /**
     * 认证器状态
     * */
    private Integer state;


}
