package com.ennew.iot.gateway.web.vo;

import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import com.ennew.iot.gateway.web.validate.EnumValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.kdla.framework.dto.PageQuery;

/**
 * @Author: alec
 * Description:
 * @date: 下午4:08 2023/7/11
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "云云对接分页查询vo")
public class CloudDockingPageQueryVo extends PageQuery {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "网关状态(enabled:启用、paused:暂停、paused:停止)")
    @EnumValid(target = NetworkConfigState.class, message = "数据验证失败，网关状态值错误")
    private String state;
}
