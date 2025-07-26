package com.ennew.iot.gateway.biz.test;

import com.ennew.iot.gateway.biz.ctwing.CtwingCloudServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestService implements CommandLineRunner {

//    @Resource
//    private CtwingCloudServer ctwingCloudServer;

    @Override
    public void run(String... args) throws Exception {

//        String msg = "{\"IMEI\":\"862806069360672\",\"IMSI\":\"undefined\",\"assocAssetId\":\"\",\"deviceId\":\"5abdcf285d4e462596853fa2340dd545\",\"deviceType\":\"\",\"messageType\":\"dataReport\",\"payload\":{\"APPdata\":\"Z0AVAWIVIP////8BIqABvOXnAKtQanZFCqzcWoxpSuVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7LeVKndzp+zy4hdbq+SRyE9NpNr+ez6EFLNVq0gft4XgowGXZKx66qiSRTQl3D7/7Lfp2hN3rD4ip/yYtR+a7GBkd5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urLpOj3SiFCYYSQUt8Kaz5P+FD4gtxJO/vMPneVEMvs0Id5S8Si+7G9NkycstwU9urgWZ/n072WBP1IRcyer6/jZlcNfr+A9Y2h3pPu0tFAW2ivu0=\"},\"productId\":\"16996520\",\"protocol\":\"lwm2m\",\"serviceId\":\"\",\"tenantId\":\"2000102221\",\"timestamp\":1700709413480,\"topic\":\"v1/up/ad\",\"upDataSN\":-1,\"upPacketSN\":-1}";
//        ctwingCloudServer.dealCloudData(msg);

    }
}
