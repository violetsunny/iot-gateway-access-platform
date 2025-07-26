package com.ennew.iot.gateway.biz.tripartite;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TripartitePlatformInfoQueryBo;
import com.ennew.iot.gateway.dal.entity.TripartitePlatformInfoEntity;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

public interface TripartitePlatformInfoService extends IService<TripartitePlatformInfoEntity> {

    PageResponse<TripartitePlatformInfoBo> queryPage(TripartitePlatformInfoPageQueryBo tripartitePlatformInfoPageQueryBo);

    boolean isExistName(String name);

    boolean isExistCode(String code);

    MultiResponse<TripartitePlatformInfoBo> list(TripartitePlatformInfoQueryBo tripartitePlatformInfoQueryBo);

}
