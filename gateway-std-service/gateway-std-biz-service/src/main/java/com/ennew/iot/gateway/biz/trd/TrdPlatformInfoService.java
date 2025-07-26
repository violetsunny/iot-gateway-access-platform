package com.ennew.iot.gateway.biz.trd;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoPageQueryBo;
import com.ennew.iot.gateway.core.bo.TrdPlatformInfoQueryBo;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;

import java.util.List;
import java.util.Map;

public interface TrdPlatformInfoService extends IService<TrdPlatformInfoEntity> {

    PageResponse<TrdPlatformInfoBo> queryPage(TrdPlatformInfoPageQueryBo trdPlatformInfoPageQueryBo);

    boolean isExistName(String name);

    TrdPlatformInfoEntity getByPCode(String pCode);

    Map<String, TrdPlatformInfoEntity> getByPCodes(List<String> pCodes);

    boolean isExistCode(String code);

    MultiResponse<TrdPlatformInfoBo> list(TrdPlatformInfoQueryBo trdPlatformInfoQueryBo);


    boolean updateConfig(Long id, String configJson);
}
