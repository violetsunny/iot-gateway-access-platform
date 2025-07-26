package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdAckEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 下行指令记录ack Mapper 接口
 * </p>
 *
 * @author lyz
 * @since 2023-03-20
 */
@Mapper
public interface EnnDownCmdAckMapper extends BaseMapper<EnnDownCmdAckEntity> {

}
