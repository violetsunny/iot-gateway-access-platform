package com.ennew.iot.gateway.starter.configuration;

import com.ennew.iot.gateway.core.repository.MinioClientRepository;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * minio配置类
 * 提供文件的上传、下载功能
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    /**
     * minio登录id
     */
    private String accessKeyId;
    /**
     * minio登录密码
     */
    private String accessKeySecret;
    /**
     * bucket名
     */
    private String bucket;
    /**
     * 上传文件的baseURL
     */
    private String resourcesUrl;
    /**
     * 外部访问查看的baseURL
     */
    private String resourcesOutUrl;

    /**
     * 注入minio 客户端
     * @return
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(resourcesUrl)
                .credentials(accessKeyId, accessKeySecret)
                .build();
    }

    @Bean
    public MinioClientRepository minioClientRepository(MinioClient minioClient) {
        return new MinioClientRepository(minioClient, resourcesOutUrl, bucket);
    }
}

