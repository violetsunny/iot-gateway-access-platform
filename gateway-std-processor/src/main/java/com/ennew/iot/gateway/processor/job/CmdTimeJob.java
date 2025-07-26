package com.ennew.iot.gateway.processor.job;


import cn.enncloud.iot.gateway.timer.annotation.EnnIotXxlJob;
import cn.enncloud.iot.gateway.timer.handler.EnnIotXxlJobHandler;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ennew.iot.gateway.biz.gateway.enums.CmdSendStatusEnum;
import com.ennew.iot.gateway.core.service.KafkaProducer;
import com.ennew.iot.gateway.core.service.RedisService;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import com.ennew.iot.gateway.dal.mapper.EnnDownCmdAckMapper;
import com.ennew.iot.gateway.dal.mapper.EnnDownCmdRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 指令下发轮询调度任务（每分钟一次）
 */
@Slf4j
@EnnIotXxlJob("CmdTimeJob")
@Component
public class CmdTimeJob  extends EnnIotXxlJobHandler {


    @Autowired
    private EnnDownCmdRecordMapper downCmdRecordMapper;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaProducer kafkaProducer;
    /**
     * 指令下发执行分布式锁前缀
     */
    public static final String DOWN_CMD_LOCK = "DOWN_CMD_LOCK:";



    @Override
    public boolean doExecute(String s) {
        log.info("===>>>执行定时下发指令调度任务");
        // 获取需要下发的指令
        QueryWrapper<EnnDownCmdRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("expect_time", new Date());
        queryWrapper.eq("send_type", "timed");
        queryWrapper.eq("send_status", CmdSendStatusEnum.accepted.getCode());
        List<EnnDownCmdRecordEntity> ennDownCmdRecords = downCmdRecordMapper.selectList(queryWrapper);
        log.debug("当前指令获取:{}", JSON.toJSONString(ennDownCmdRecords));

        ennDownCmdRecords.forEach(cmd -> {

            // 数据库更新指令状态
            cmd.setSendStatus(CmdSendStatusEnum.processing.getCode());
            downCmdRecordMapper.updateById(cmd);

            // 指令添加分布式锁
            String seq = cmd.getSeq();
            String lockKey = DOWN_CMD_LOCK + seq;
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, cmd.getDevId(), 30, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(aBoolean)) {
                // 执行下发指令
                boolean send = kafkaProducer.send("device_command_topic", cmd.getContent());
                if (send) {
                    // 更新指令执行进度
                    cmd.setSendStatus(CmdSendStatusEnum.processing.getCode());
                    cmd.setSendTime(new Date());
                    downCmdRecordMapper.updateById(cmd);
                }

            }

        });

        return true;
    }

}
