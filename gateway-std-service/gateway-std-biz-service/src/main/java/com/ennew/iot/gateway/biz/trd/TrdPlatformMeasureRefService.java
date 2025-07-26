package com.ennew.iot.gateway.biz.trd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;

public interface TrdPlatformMeasureRefService extends IService<TrdPlatformMeasureRefEntity> {

    String entityParamCheck(TrdPlatformMeasureRefEntity trdPlatformMeasureRefEntity);

}
