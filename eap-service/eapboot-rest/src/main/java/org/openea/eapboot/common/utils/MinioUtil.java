package org.openea.eapboot.common.utils;

import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.modules.base.vo.OssSetting;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import io.minio.MinioClient;
import io.minio.policy.PolicyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;


/**
 */
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public OssSetting getOssSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.MINIO_OSS);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("您还未配置MINIO");
        }
        return new Gson().fromJson(v, OssSetting.class);
    }

    /**
     * 文件路径上传
     * @param filePath
     * @param key
     * @return
     */
    public String minioUpload(String filePath, String key) throws Exception {

        OssSetting os = getOssSetting();
        MinioClient minioClient = new MinioClient(os.getHttp()+os.getEndpoint(), os.getAccessKey(), os.getSecretKey());
        checkBucket(os, minioClient);
        minioClient.putObject(os.getBucket(), key, filePath);
        return os.getHttp() + os.getEndpoint() + "/" + os.getBucket() + "/" + key;
    }

    /**
     * 文件流上传
     * @param inputStream
     * @param key
     * @return
     */
    public String minioInputStreamUpload(InputStream inputStream, String key) throws Exception {

        OssSetting os = getOssSetting();
        MinioClient minioClient = new MinioClient(os.getHttp()+os.getEndpoint(), os.getAccessKey(), os.getSecretKey());
        checkBucket(os, minioClient);
        minioClient.putObject(os.getBucket(), key, inputStream, "application/octet-stream;charset=UTF-8");
        return os.getHttp() + os.getEndpoint() + "/" + os.getBucket() + "/" + key;
    }

    /**
     * 重命名
     * @param fromKey
     * @param toKey
     */
    public String renameFile(String fromKey, String toKey) throws Exception{

        OssSetting os = getOssSetting();
        copyFile(fromKey, toKey);
        deleteFile(fromKey);
        return os.getHttp() + os.getEndpoint() + "/" + os.getBucket() + "/" + toKey;
    }

    /**
     * 复制文件
     * @param fromKey
     */
    public String copyFile(String fromKey, String toKey) throws Exception {

        OssSetting os = getOssSetting();
        MinioClient minioClient = new MinioClient(os.getHttp()+os.getEndpoint(), os.getAccessKey(), os.getSecretKey());
        checkBucket(os, minioClient);
        minioClient.copyObject(os.getBucket(), fromKey, os.getBucket(), toKey);
        return os.getHttp() + os.getEndpoint() + "/" + os.getBucket() + "/" + toKey;
    }

    /**
     * 删除文件
     * @param key
     */
    public void deleteFile(String key) throws Exception {

        OssSetting os = getOssSetting();
        MinioClient minioClient = new MinioClient(os.getHttp()+os.getEndpoint(), os.getAccessKey(), os.getSecretKey());
        checkBucket(os, minioClient);
        minioClient.removeObject(os.getBucket(), key);
    }

    /**
     * 如果存储桶不存在 创建存储通
     * @param os
     * @param minioClient
     * @throws Exception
     */
    public void checkBucket(OssSetting os, MinioClient minioClient) throws Exception {

        // 如果存储桶不存在 创建存储通
        if(!minioClient.bucketExists(os.getBucket())){
            minioClient.makeBucket(os.getBucket());
            minioClient.setBucketPolicy(os.getBucket(), "*", PolicyType.READ_ONLY);
        }
    }
}
