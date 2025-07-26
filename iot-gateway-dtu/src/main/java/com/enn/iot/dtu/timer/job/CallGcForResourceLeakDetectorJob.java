package com.enn.iot.dtu.timer.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static com.enn.iot.dtu.common.constant.IotCommonsConstant.PROPERTIES_KEY_ENV;
import static com.enn.iot.dtu.common.constant.IotCommonsConstant.PROPERTIES_KEY_ENV_VALUE_DEV;

/**
 * 测试联调期间使用，For test ResourceLeakDetector <br>
 * 定时触发垃圾回收 <br>
 * 只在"DEV"环境执行此定时任务
 * @author lixiangk
 * @version 1.0 2022/05/20
 */
@Slf4j
@Component
public class CallGcForResourceLeakDetectorJob implements Runnable {

    private final Environment environment;

    public CallGcForResourceLeakDetectorJob(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        String envValue = environment.getProperty(PROPERTIES_KEY_ENV);
        if (StringUtils.equals(envValue, PROPERTIES_KEY_ENV_VALUE_DEV)) {
            System.gc();
        }
    }
}