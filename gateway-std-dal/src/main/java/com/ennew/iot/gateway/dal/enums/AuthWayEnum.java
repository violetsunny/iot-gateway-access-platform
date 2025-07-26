package com.ennew.iot.gateway.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 认证方式
 * @Author: qinkun
 * @Date: 2021/11/1 15:21
 */
@Getter
@AllArgsConstructor
public enum AuthWayEnum {

	NO("无", 1),

	TOKEN("TOKEN", 2);

	@Getter
	final String name;
	final int code;

	public Integer getCode() {
		return code;
	}

}
