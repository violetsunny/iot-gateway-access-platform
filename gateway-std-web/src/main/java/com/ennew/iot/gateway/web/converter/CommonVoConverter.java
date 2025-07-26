/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.web.converter;

import com.ennew.iot.gateway.core.bo.KeyValueBo;
import com.ennew.iot.gateway.web.vo.KeyValueVo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author kanglele
 * @version $Id: CommonVoConverter, v 0.1 2023/11/15 15:37 kanglele Exp $
 */
@Mapper(componentModel = "spring")
public interface CommonVoConverter {

    List<KeyValueVo<String>> toCommonVo(List<KeyValueBo<String>> kv);
}
