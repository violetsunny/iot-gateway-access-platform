package com.ennew.iot.gateway.core.bo;

import com.ennew.iot.gateway.dal.entity.TrdPlatformApiEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiParamEntity;
import lombok.Data;

import java.util.List;

@Data
public class TrdPlatformApiBo extends TrdPlatformApiEntity {

    List<TrdPlatformApiParamEntity> apiParams;

}
