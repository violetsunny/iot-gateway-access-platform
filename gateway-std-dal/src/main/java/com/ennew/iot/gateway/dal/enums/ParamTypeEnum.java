package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 参数类型
 * @Author: cfl
 * @Date: 2021/11/4 16:16
 */
@Getter
@AllArgsConstructor
public enum ParamTypeEnum {

	NO("无", 1),

	FIXED("固定值", 2),

	MAPPING_PASS("参数替换", 3),

	GROOVY("动态groovy脚本", 4),

	SPECIAL_PASS("特殊参数替换", 5);

	@Getter
	final String name;
	final int code;

	public Integer getCode() {
		return code;
	}

}
