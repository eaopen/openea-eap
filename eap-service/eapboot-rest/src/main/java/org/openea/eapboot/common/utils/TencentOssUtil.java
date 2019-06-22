package org.openea.eapboot.common.utils;

import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.modules.base.vo.OssSetting;
import org.openea.eapboot.common.exception.EapbootException;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Copy;
import com.qcloud.cos.transfer.TransferManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 */
@Component
@Slf4j
public class TencentOssUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public OssSetting getOssSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.TENCENT_OSS);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("您还未配置腾讯云COS");
        }
        return new Gson().fromJson(v, OssSetting.class);
    }

    /**
     * 文件路径上传
     * @param filePath
     * @param key
     * @return
     */
    public String tencentUpload(String filePath, String key) {

        OssSetting os = getOssSetting();

        COSCredentials cred = new BasicCOSCredentials(os.getAccessKey(), os.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(os.getBucketRegion()));
        COSClient cosClient = new COSClient(cred, clientConfig);

        PutObjectRequest putObjectRequest = new PutObjectRequest(os.getBucket(), key, new File(filePath));
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return os.getHttp() + os.getEndpoint() + "/" + key;
    }

    /**
     * 文件流上传
     * @param file
     * @param inputStream
     * @param key
     * @return
     */
    public String tencentInputStreamUpload(MultipartFile file, InputStream inputStream, String key) {

        OssSetting os = getOssSetting();

        COSCredentials cred = new BasicCOSCredentials(os.getAccessKey(), os.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(os.getBucketRegion()));
        COSClient cosClient = new COSClient(cred, clientConfig);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(os.getBucket(), key, inputStream, objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return os.getHttp() + os.getEndpoint() + "/" + key;
    }

    /**
     * 重命名
     * @param fromKey
     * @param toKey
     */
    public String renameFile(String fromKey, String toKey){

        OssSetting os = getOssSetting();
        copyFile(fromKey, toKey);
        deleteFile(fromKey);
        return os.getHttp() + os.getEndpoint() + "/" + toKey;
    }

    /**
     * 复制文件
     * @param fromKey
     */
    public String copyFile(String fromKey, String toKey){

        OssSetting os = getOssSetting();

        COSCredentials cred = new BasicCOSCredentials(os.getAccessKey(), os.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(os.getBucketRegion()));
        COSClient cosClient = new COSClient(cred, clientConfig);

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(os.getBucket(), fromKey, os.getBucket(), toKey);

        TransferManager transferManager = new TransferManager(cosClient);
        try {
            Copy copy = transferManager.copy(copyObjectRequest, cosClient, null);
            CopyResult copyResult = copy.waitForCopyResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new EapbootException("复制文件失败");
        }
        transferManager.shutdownNow();
        cosClient.shutdown();
        return os.getHttp() + os.getEndpoint() + "/" + toKey;
    }

    /**
     * 删除文件
     * @param key
     */
    public void deleteFile(String key){

        OssSetting os = getOssSetting();

        COSCredentials cred = new BasicCOSCredentials(os.getAccessKey(), os.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(os.getBucketRegion()));
        COSClient cosClient = new COSClient(cred, clientConfig);

        cosClient.deleteObject(os.getBucket(), key);
        cosClient.shutdown();
    }
}
