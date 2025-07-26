package com.ennew.iot.gateway.biz.trd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformApiQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformApiEntity;
import top.kdla.framework.dto.MultiResponse;

public interface TrdPlatformApiService extends IService<TrdPlatformApiEntity> {

    MultiResponse<TrdPlatformApiBo> list(TrdPlatformApiQueryBo trdPlatformApiQueryBo);

    TrdPlatformApiBo getDetailById(String id);

    Boolean saveApi(TrdPlatformApiBo trdPlatformApiBo);

    Boolean updateApiById(TrdPlatformApiBo trdPlatformApiBo);

    boolean isExistName(String code,String name);

    boolean isExistCode(String code);

    MultiResponse<TrdPlatformApiBo> listApi(TrdPlatformApiQueryBo trdPlatformApiQueryBo);

    Boolean removeApiById(Long id);

}
