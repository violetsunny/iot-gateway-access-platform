package com.ennew.iot.gateway.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * <p>
 * 下行指令记录表 Mapper 接口
 * </p>
 *
 * @author lyz
 * @since 2023-03-20
 */
@Mapper
public interface EnnDownCmdRecordMapper extends BaseMapper<EnnDownCmdRecordEntity> {

//    @Select("select r.* ,a.seq is not null acked from enn_down_cmd_record r " +
//            "left join enn_down_cmd_ack a on a.seq = r.seq and a.dev_id=r.dev_id "+
//            "${ew.customSqlSegment}")
    @Select("<script> " +
            "select r.* ,a.seq is not null acked from enn_down_cmd_record r " +
            "left join enn_down_cmd_ack a on a.seq = r.seq and a.dev_id=r.dev_id "+
            "<where>"+
            "<if test='devId != null and devId != \"\"'> and r.dev_id = #{devId} </if>" +
            "<if test='type != null and type != \"\"'> and r.cmd_type = #{type} </if>" +
            "<if test='seq != null and seq != \"\"'> and r.seq = #{seq} </if>" +
            "<if test='source != null and source != \"\"'> and r.source = #{source} </if>" +
            "<if test='tenantId != null and tenantId != \"\"'> and r.tenant_id = #{tenantId} </if>" +
            "<if test='sendTimeRangeStart != null'> and (r.send_time &gt;= #{sendTimeRangeStart} or r.expect_time &gt;= #{sendTimeRangeStart}) </if>" +
            "<if test='sendTimeRangeEnd != null'> and (r.send_time &lt;= #{sendTimeRangeEnd} or r.expect_time &lt;= #{sendTimeRangeEnd}) </if>" +
            "<if test='serviceCode != null'> and r.service_code = #{serviceCode} </if>" +
            "</where>"+
            "</script>")
    Page<EnnDownCmdRecordEntity> selectPageWithAcked(Page<EnnDownCmdRecordEntity> page, @Param("devId") String devId, @Param("type")String type, @Param("seq")String seq,
                                                     @Param("source")String source, @Param("tenantId")String tenantId, @Param("sendTimeRangeStart")Date sendTimeRangeStart, @Param("sendTimeRangeEnd")Date sendTimeRangeEnd, @Param("serviceCode")String serviceCode);


}
