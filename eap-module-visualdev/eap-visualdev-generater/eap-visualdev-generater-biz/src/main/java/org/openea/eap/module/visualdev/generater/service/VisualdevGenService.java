package org.openea.eap.module.visualdev.generater.service;


import org.openea.eap.module.visualdev.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.base.model.DownloadCodeForm;

/**
 *
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司（https://www.jnpfsoft.com）
 * @author JNPF开发平台组
 * @date 2021/3/16
 */
public interface VisualdevGenService extends SuperService<VisualdevEntity> {

    /**
     * 代码生成
     * @param visualdevEntity 可视化开发功能
     * @param downloadCodeForm 下载相关信息
     * @return 下载文件名
     * @throws Exception ignore
     */
    String codeGengerate(VisualdevEntity visualdevEntity, DownloadCodeForm downloadCodeForm) throws Exception;
}

