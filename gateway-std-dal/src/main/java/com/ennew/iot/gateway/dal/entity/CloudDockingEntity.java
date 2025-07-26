package com.ennew.iot.gateway.dal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @Author: alec
 * Description:
 * @date: 上午10:32 2023/7/12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="cloud_docking_base", autoResultMap = true)
public class CloudDockingEntity {

    @TableId(value = "id")
    private String id;

    private String code;

    private String name;

    private String baseUrl;

    private String state;

    private Date createTime;

}
