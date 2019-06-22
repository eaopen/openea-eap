package org.openea.eapboot.common.utils;

import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.modules.base.vo.OssSetting;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 */
@Slf4j
@Component
public class QiniuUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public OssSetting getOssSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.QINIU_OSS);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("您还未配置七牛云OSS");
        }
        return new Gson().fromJson(v, OssSetting.class);
    }

    public Configuration getConfiguration(Integer zone){

        Configuration cfg = null;
        if(zone.equals(SettingConstant.ZONE_ZERO)){
            cfg = new Configuration(Zone.zone0());
        }else if(zone.equals(SettingConstant.ZONE_ONE)){
            cfg = new Configuration(Zone.zone1());
        }else if(zone.equals(SettingConstant.ZONE_TWO)){
            cfg = new Configuration(Zone.zone2());
        }else if(zone.equals(SettingConstant.ZONE_THREE)){
            cfg = new Configuration(Zone.zoneNa0());
        }else if(zone.equals(SettingConstant.ZONE_FOUR)){
            cfg = new Configuration(Zone.zoneAs0());
        }else {
            cfg = new Configuration(Zone.autoZone());
        }
        return cfg;
    }

    public UploadManager getUploadManager(Configuration cfg){

        UploadManager uploadManager = new UploadManager(cfg);
        return uploadManager;
    }

    /**
     * 文件路径上传
     * @param filePath
     * @param key 文件名
     * @return
     */
    public String qiniuUpload(String filePath, String key) {

        OssSetting os = getOssSetting();
        Auth auth = Auth.create(os.getAccessKey(), os.getSecretKey());
        String upToken = auth.uploadToken(os.getBucket());
        try {
            Response response = getUploadManager(getConfiguration(os.getZone())).put(filePath, key, upToken);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return os.getHttp() + os.getEndpoint() + "/" + putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            throw new EapbootException("上传文件出错，请检查七牛云配置，" + r.toString());
        }
    }

    /**
     * 文件流上传
     * @param inputStream
     * @param key  文件名
     * @return
     */
    public String qiniuInputStreamUpload(InputStream inputStream, String key) {

        OssSetting os = getOssSetting();
        Auth auth = Auth.create(os.getAccessKey(), os.getSecretKey());
        String upToken = auth.uploadToken(os.getBucket());
        try {
            Response response = getUploadManager(getConfiguration(os.getZone())).put(inputStream, key, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return os.getHttp() + os.getEndpoint() + "/" + putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            throw new EapbootException("上传文件出错，请检查七牛云配置，" + r.toString());
        }
    }

    /**
     * 重命名
     * @param fromKey
     * @param toKey
     */
    public String renameFile(String fromKey, String toKey){

        OssSetting os = getOssSetting();
        Auth auth = Auth.create(os.getAccessKey(), os.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, getConfiguration(os.getZone()));
        try {
            bucketManager.move(os.getBucket(), fromKey, os.getBucket(), toKey);
            return os.getHttp() + os.getEndpoint() + "/" + toKey;
        } catch (QiniuException ex) {
            throw new EapbootException("重命名文件失败，" + ex.response.toString());
        }
    }

    /**
     * 复制文件
     * @param fromKey
     */
    public String copyFile(String fromKey, String toKey){

        OssSetting os = getOssSetting();
        Auth auth = Auth.create(os.getAccessKey(), os.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, getConfiguration(os.getZone()));
        try {
            bucketManager.copy(os.getBucket(), fromKey, os.getBucket(), toKey);
            return os.getHttp() + os.getEndpoint() + "/" + toKey;
        } catch (QiniuException ex) {
            throw new EapbootException("复制文件失败，" + ex.response.toString());
        }
    }

    /**
     * 删除文件
     * @param key
     */
    public void deleteFile(String key){

        OssSetting os = getOssSetting();
        Auth auth = Auth.create(os.getAccessKey(), os.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, getConfiguration(os.getZone()));
        try {
            bucketManager.delete(os.getBucket(), key);
        } catch (QiniuException ex) {
            throw new EapbootException("删除文件失败，" + ex.response.toString());
        }
    }
}