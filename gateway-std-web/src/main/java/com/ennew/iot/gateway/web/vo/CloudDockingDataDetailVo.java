package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午2:52 2023/7/20
 */
@Schema(description = "CloudDockingDataDetailVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingDataDetailVo {

    private CloudDockingResVo platform;

    private List<CloudDockingDataCmdVo> baseInfo;

}
