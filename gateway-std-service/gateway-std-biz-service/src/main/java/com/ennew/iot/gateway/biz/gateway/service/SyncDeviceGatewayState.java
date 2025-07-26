package com.ennew.iot.gateway.biz.gateway.service;

import com.ennew.iot.gateway.biz.gateway.supports.DeviceGatewayManager;
import com.ennew.iot.gateway.core.repository.DeviceGatewayRepository;
import com.ennew.iot.gateway.dal.entity.DeviceGatewayEntity;
import com.ennew.iot.gateway.dal.enums.NetworkConfigState;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;

@Order(2)
@Component
@Slf4j
public class SyncDeviceGatewayState implements CommandLineRunner {

    @Autowired
    private DeviceGatewayRepository deviceGatewayRepository;

    @Autowired
    private DeviceGatewayManager deviceGatewayManager;

    private final Duration gatewayStartupDelay = Duration.ofSeconds(5);

    @SneakyThrows
    @Override
    public void run(String... args) {
//        new Thread(() -> {
//            try {
//                this.run();
//            }catch (InterruptedException interruptedException){
//                Thread.currentThread().interrupt();
//                log.warn("SyncDeviceGatewayState 异常", interruptedException);
//            } catch (Exception e) {
//                log.warn("SyncDeviceGatewayState 异常", e);
//            }
//        }).start();
    }

    private void run() throws InterruptedException {
        log.debug("start device gateway");
        Thread.sleep(gatewayStartupDelay.toMillis());
        List<DeviceGatewayEntity> entities = deviceGatewayRepository.lambdaQuery()
                .select(DeviceGatewayEntity::getId)
                .eq(DeviceGatewayEntity::getState, NetworkConfigState.enabled).list();
        if (!CollectionUtils.isEmpty(entities)) {
            entities.forEach(entity -> deviceGatewayManager.getGateway(entity.getId()).startup());
        }
    }

}
