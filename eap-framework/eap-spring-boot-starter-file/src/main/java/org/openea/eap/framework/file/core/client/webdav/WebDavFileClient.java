package org.openea.eap.framework.file.core.client.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.openea.eap.framework.file.core.client.AbstractFileClient;

import java.io.File;
import java.io.IOException;

/**
 * webdav客户端
 *
 * 集成Sardine
 * https://github.com/lookfirst/sardine
 *
 * todo 未完成
 */
public class WebDavFileClient extends AbstractFileClient<WebDavFileClientConfig> {

    private Sardine client;

    public WebDavFileClient(Long id, WebDavFileClientConfig config) {
        super(id, config);
    }

    /**
     * 自定义初始化
     */
    @Override
    protected void doInit() {
        if (!config.getBasePath().endsWith(File.separator)) {
            config.setBasePath(config.getBasePath() + File.separator);
        }
        try {
            if(this.client!=null) {
                this.client.shutdown();
            }
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }

        this.client = SardineFactory.begin(config.getUser(), config.getPassword());
    }

    /**
     * 上传文件
     *
     * @param content 文件流
     * @param path    相对路径
     * @param type
     * @return 完整路径，即 HTTP 访问地址
     * @throws Exception 上传文件时，抛出 Exception 异常
     */
    @Override
    public String upload(byte[] content, String path, String type) throws Exception {
        return null;
    }

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @throws Exception 删除文件时，抛出 Exception 异常
     */
    @Override
    public void delete(String path) throws Exception {

    }

    /**
     * 获得文件的内容
     *
     * @param path 相对路径
     * @return 文件的内容
     */
    @Override
    public byte[] getContent(String path) throws Exception {
        return new byte[0];
    }
}
