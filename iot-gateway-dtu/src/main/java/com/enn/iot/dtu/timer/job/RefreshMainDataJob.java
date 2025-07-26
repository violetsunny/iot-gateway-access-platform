package com.enn.iot.dtu.timer.job;

import com.enn.iot.dtu.service.MainDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description DTU主数据变化更新定时作业
 * @Author nixiaolin
 * @Date 2021/11/2 14:11
 */
@Slf4j
@Component
public class RefreshMainDataJob implements Runnable {

    private final MainDataService mainDataService;

    public RefreshMainDataJob(MainDataService mainDataService) {
        this.mainDataService = mainDataService;
    }

    @Override
    public void run() {
        try {
            mainDataService.refreshAllMainDataIf();
        } catch (Exception e) {
            log.error("[JOB] 刷新配置异常！", e);
        }
    }
}