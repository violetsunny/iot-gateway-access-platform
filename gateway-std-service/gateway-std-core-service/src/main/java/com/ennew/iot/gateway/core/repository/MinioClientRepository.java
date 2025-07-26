package com.ennew.iot.gateway.core.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.ennew.iot.gateway.core.bo.FileBo;
import com.ennew.iot.gateway.core.bo.ObjectItem;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.kdla.framework.exception.BizException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class MinioClientRepository {

    private final MinioClient minioClient;

    private final String resourcesOutUrl;

    private final String bucket;

    public boolean existBucket(String name) {

        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        } catch (Exception e) {
            log.error("existBucket error", e);
        }
        return false;

    }

    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("makeBucket error", e);
            return false;
        }
        return true;
    }

    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("removeBucket error", e);
            return false;
        }
        return true;
    }

    public String uploadOne(FileBo file) {
        // 判断存储桶是否存在
        if (!existBucket(bucket)) {
            makeBucket(bucket);
        }
        String filename = generateFileName(file.getFilename());
        try {
            PutObjectArgs putObjectRequest = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .build();
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回UploadResult, 失败抛出异常
            minioClient.putObject(putObjectRequest);
            return resourcesOutUrl + "/" + bucket + "/" + filename;
        } catch (Exception e) {
            log.error("uploadFileMinio fail", e);
            throw new BizException("file upload fail");
        }
    }

    public String generateFileName(String oriName) {
        return DateUtil.format(new Date(), "yyyyMMdd") + UUID.randomUUID().toString().replaceAll("-", "") + "." + FileUtil.extName(oriName);
    }

    public List<ObjectItem> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<ObjectItem> objectItems = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                ObjectItem objectItem = new ObjectItem();
                objectItem.setObjectName(item.objectName());
                objectItem.setSize(item.size());
                objectItems.add(objectItem);
            }
        } catch (Exception e) {
            log.error("listObjects error", e);
            return null;
        }
        return objectItems;
    }

    public Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> objects) {
        List<DeleteObject> dos = objects.stream().map(DeleteObject::new).collect(Collectors.toList());
        return minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
    }


}


