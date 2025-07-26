package com.ennew.iot.gateway.biz.trd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformModelRefQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

public interface TrdPlatformModelRefService extends IService<TrdPlatformModelRefEntity> {

    PageResponse<TrdPlatformModelRefBo> queryPage(TrdPlatformModelRefPageQueryBo trdPlatformModelRefPageQueryBo);

    String entityParamCheck(TrdPlatformModelRefEntity trdPlatformModelRefEntity);

    MultiResponse<TrdPlatformModelRefBo> list(TrdPlatformModelRefQueryBo trdPlatformModelRefQueryBo);

}
