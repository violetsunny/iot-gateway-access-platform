package com.ennew.iot.gateway.core.repository;

import com.ennew.iot.gateway.core.bo.FileBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileRepository {

    @Autowired
    private MinioClientRepository minioMapper;

    public String uploadFile(FileBo file) {
        return minioMapper.uploadOne(file);
    }
}
