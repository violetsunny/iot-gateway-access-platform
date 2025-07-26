package com.ennew.iot.gateway.biz.trd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformTaskQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformTaskEntity;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

public interface TrdPlatformTaskService extends IService<TrdPlatformTaskEntity> {

    PageResponse<TrdPlatformTaskBo> queryPage(TrdPlatformTaskPageQueryBo trdPlatformTaskPageQueryBo);

    boolean isExistName(String platformCode,String name);

    boolean isExistCode(String platformCode,String code);

    MultiResponse<TrdPlatformTaskBo> list(TrdPlatformTaskQueryBo trdPlatformTaskQueryBo);

    Boolean saveTask(TrdPlatformTaskEntity entity);

    Boolean updateTask(TrdPlatformTaskEntity updateEntity);

    Boolean removeTask(Long id);

}
