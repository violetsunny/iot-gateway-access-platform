package com.ennew.iot.gateway.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * @Author: alec
 * Description:
 * @date: 下午6:18 2023/7/12
 */
@Schema(description = "CloudDockingDetailVo对象")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CloudDockingAuthDetailVo {

   private CloudDockingResVo platform;

   private CloudDockingAuthCmdVo baseInfo;

   private List<CloudDockingParamsCmdVo> params;

   private CloudDockingAuthResCmdVo res;

}


