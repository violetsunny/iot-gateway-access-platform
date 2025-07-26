package com.ennew.iot.gateway.processor.job;


import cn.enncloud.iot.gateway.timer.annotation.EnnIotXxlJob;
import cn.enncloud.iot.gateway.timer.handler.EnnIotXxlJobHandler;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ennew.iot.gateway.biz.gateway.enums.CmdSendStatusEnum;
import com.ennew.iot.gateway.dal.entity.EnnDownCmdRecordEntity;
import com.ennew.iot.gateway.dal.mapper.EnnDownCmdRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 指令下发监控轮询调度任务（每3小时一次）
 */
@Slf4j
@EnnIotXxlJob("CmdMonitorJob")
@Component
public class CmdMonitorJob  extends EnnIotXxlJobHandler {


    @Autowired
    private EnnDownCmdRecordMapper downCmdRecordMapper;


    @Value("${cmd.icomeRobotUrl:https://oapi.dingtalk.com/robot/send?access_token=1a65359c6f2fdee24c4c348b6ef726a54d328e1a1da616fe224f4192f30411d9}")
    private String icomeRobotUrl;


    public void test() {
        doExecute("ss");
    }

    @Override
    public boolean doExecute(String s) {


        // TODO:指令下发相关功能，需要网关管理模块完善，数据库字段表调整
        // 获取下发异常状态的指令
        QueryWrapper<EnnDownCmdRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("expect_time", new Date());
        queryWrapper.eq("send_type", "timed");
        queryWrapper.eq("send_status", CmdSendStatusEnum.processing.getCode());
        queryWrapper.orderByDesc("create_time");
        List<EnnDownCmdRecordEntity> ennDownCmdRecords = downCmdRecordMapper.selectList(queryWrapper);
        log.info("处理中下发指令获取：{}", JSON.toJSONString(ennDownCmdRecords));

        StringBuilder contentInfo = new StringBuilder();


        if (CollectionUtils.isEmpty(ennDownCmdRecords)) {
            log.info("处理中下发指令条数：0");
            contentInfo.append("## 监控状态正常 \n处理中指令0条\n");
        } else {
            int processSize = ennDownCmdRecords.size();
            contentInfo.append("## 监控状态异常 \n处理中指令(");
            contentInfo.append(processSize);
            contentInfo.append(")条 \n");
            contentInfo.append("最近创建10条内指令: \n");

            // 示例数据
            List<Object[]> data = new ArrayList<>();
            // 异常信息处理
            ennDownCmdRecords.forEach(cmd -> {
                // 报警异常状态的指令信息
                if (data.size() > 9) {
                    return;
                }
                data.add(new Object[]{cmd.getSeq(), cmd.getDevId(), cmd.getExpectTime()});
            });
            contentInfo.append(convertToMarkdownTable(data));
        }
        // 发送icome告警监控
        sendIcomeRobot(contentInfo.toString());
        return true;
    }

    /**
     * 发送icome告警信息
     */
    private void sendIcomeRobot(String contentInfo) {

        if (StringUtils.isBlank(contentInfo)) {
            return;
        }
        String contentInfoTitle = "指令下发异常信息监控";
        JSONObject content = new JSONObject();
        content.put("title", contentInfoTitle);
        content.put("text", contentInfo);
        JSONObject req = new JSONObject();
        req.put("msgtype", "markdown");
        req.put("markdown", content);

        try {
            String post = HttpUtil.post(icomeRobotUrl, JSON.toJSONString(req));
            log.info("指令异常监控发送结果：{}", post);
        } catch (Exception e) {
            log.error("指令异常监控发送异常:", e.fillInStackTrace());
        }
    }


    private static String convertToMarkdownTable(List<Object[]> data) {
        StringBuilder sb = new StringBuilder();

        // 获取表格列数
        int numColumns = data.get(0).length;

        // 添加表头
        sb.append("| 序列号 | 设备id | 期望下发时间 |\n");

        // 添加表头分隔线
        for (int i = 0; i < numColumns; i++) {
            sb.append("|");
            sb.append(" --- ");
        }
        sb.append("|\n");

        // 添加数据行
        for (Object[] row : data) {
            for (Object cell : row) {
                sb.append("|");
                sb.append(Objects.nonNull(cell) ? cell.toString() : "");
            }
            sb.append("|\n");
        }

        return sb.toString();
    }
}
